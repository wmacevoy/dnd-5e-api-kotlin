"""Decide whether a change is light work: no shape change, only the behaviour its task names.

    python scripts/gate.py --base REF --head DIR [--allow REGEX]

REF is the commit the change starts from; DIR is a checkout with the change committed. REGEX
matches the golden requests the task is allowed to change (case-insensitive); with none, no
recorded answer may change. Prints PASS or FAIL with every reason, each marked as a shape change (for the owner) or as work
not done (back to the builder), and exits 0 or 1.

A change is light work when all of these hold:
  - no dependency, build, requirements, or vendored file changed, and no new top-level directory;
  - new main source files go only into packages that already exist;
  - `pixi run check` passes, and so does `./se --doctor`;
  - the published surface is unchanged: the MCP tool list and the OpenAPI document, from builds
    of REF and of DIR;
  - every recorded golden answer of REF that changed matches REGEX.

It needs the seeded MongoDB running. It uses ports 18090 and 18091 and stops what it starts.
"""
import argparse
import pathlib
import re
import shutil
import socket
import subprocess
import sys
import tempfile
import time
import urllib.request

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import golden  # noqa: E402  (the comparison is golden.py's own, so the two cannot disagree)

ROOT = pathlib.Path(__file__).resolve().parents[1]
JAVA = ROOT / ".pixi/envs/default/bin/java"
JAR = "target/dnd-5e-api-0.0.1-SNAPSHOT.jar"
FORBIDDEN = re.compile(r"^(pom\.xml|pixi\.toml|pixi\.lock|mvnw.*|\.mvn/|docs/REQUIREMENTS_|vendor/|AGENTS\.md|se$|se\.py|se\.ps1)")
MAIN_SOURCE = "src/main/"
SHAPE = "shape change, for the owner: "
NOT_DONE = "not done, back to the builder: "


def run(command, cwd, quiet=True):
    result = subprocess.run(command, cwd=cwd, capture_output=quiet, text=True)
    return result.returncode, (result.stdout or "") + (result.stderr or "")


def pixi():
    return shutil.which("pixi") or str(pathlib.Path.home() / ".pixi/bin/pixi")


def file_reasons(head, base):
    """Reasons from the changed files alone."""
    reasons = []
    code, status = run(["git", "status", "--porcelain"], head)
    if status.strip():
        reasons.append(NOT_DONE + "uncommitted changes in the checkout; the gate judges a commit")
    code, listing = run(["git", "diff", "--name-status", base, "HEAD"], head)
    if code:
        return reasons + [NOT_DONE + f"git diff failed: {listing.strip()}"]
    code, tree = run(["git", "ls-tree", "-r", "--name-only", base], head)
    old_paths = tree.split()
    old_dirs = {str(pathlib.PurePosixPath(p).parent) for p in old_paths}
    old_top = {p.split("/")[0] for p in old_paths}
    for line in listing.splitlines():
        change, path = line.split("\t")[0], line.split("\t")[-1]
        if FORBIDDEN.match(path):
            reasons.append(SHAPE + f"changes {path}, which is a dependency, build, requirements, or vendored file")
        if path.split("/")[0] not in old_top:
            reasons.append(SHAPE + f"adds a top-level directory: {path.split('/')[0]}")
        if change.startswith("A") and path.startswith(MAIN_SOURCE):
            folder = str(pathlib.PurePosixPath(path).parent)
            if folder not in old_dirs:
                reasons.append(SHAPE + f"adds main source in a new package: {folder}")
    return reasons


def build(directory):
    code, output = run([pixi(), "run", "sh", "./mvnw", "-B", "-q", "package", "-DskipTests"], directory)
    if code:
        raise RuntimeError(f"build failed in {directory}:\n{output[-2000:]}")


class Server:
    """A server this gate started, from a built checkout, stopped by its own process handle."""

    def __init__(self, directory, port):
        self.base = f"http://127.0.0.1:{port}"
        self.log = open(pathlib.Path(tempfile.gettempdir()) / f"gate-{port}.log", "w")
        self.process = subprocess.Popen(
            [str(JAVA), "-jar", JAR, "--server.address=127.0.0.1", f"--server.port={port}"],
            cwd=directory, stdout=self.log, stderr=subprocess.STDOUT,
        )
        for _ in range(120):
            try:
                urllib.request.urlopen(self.base + "/v3/api-docs", timeout=5)
                return
            except OSError:
                if self.process.poll() is not None:
                    raise RuntimeError(f"server on {port} exited; see {self.log.name}")
                time.sleep(1)
        self.stop()
        raise RuntimeError(f"server on {port} did not answer in 120 seconds")

    def stop(self):
        self.process.terminate()
        try:
            self.process.wait(timeout=30)
        except subprocess.TimeoutExpired:
            self.process.kill()
        self.log.close()


def surface(server, session):
    """The published surface: the OpenAPI document and the MCP tool list, as digests."""
    documented = golden.run_case(server.base, None, "http", "/v3/api-docs")[1]
    tools = golden.run_case(server.base, session, "mcp-tools", "tools/list")[1]
    return golden.digest(documented), golden.digest(tools)


def behaviour_reasons(head, base, allow):
    reasons = []
    worktree = pathlib.Path(tempfile.mkdtemp(prefix="gate-base-"))
    code, output = run(["git", "worktree", "add", "--detach", str(worktree), base], head)
    if code:
        return [NOT_DONE + f"could not check out {base}: {output.strip()}"]
    servers = []
    try:
        build(worktree)
        build(head)
        before = Server(worktree, 18090)
        servers.append(before)
        after = Server(head, 18091)
        servers.append(after)
        before_session = golden.McpSession(before.base)
        session = golden.McpSession(after.base)
        old_doc, old_tools = surface(before, before_session)
        new_doc, new_tools = surface(after, session)
        if old_doc != new_doc:
            reasons.append(SHAPE + "the OpenAPI document changed: a path, parameter, or schema was added or altered")
        if old_tools != new_tools:
            reasons.append(SHAPE + "the MCP tool list changed: a tool was added, removed, or reshaped")
        code, manifest = run(["git", "show", f"{base}:golden/manifest.tsv"], head)
        changed, outside = 0, []
        for line in manifest.splitlines():
            group, status, sha, kind, request = line.split("\t", 4)
            if group == "schema" or kind == "mcp-tools":
                continue  # judged above, from both builds
            actual_status, value = golden.run_case(after.base, session, kind, request)
            if str(actual_status) == status and (sha == golden.UNCHECKED or golden.digest(value) == sha):
                continue
            changed += 1
            if not (allow and allow.search(request)):
                outside.append(request)
        if outside:
            sample = "; ".join(outside[:5]) + (f"; and {len(outside) - 5} more" if len(outside) > 5 else "")
            reasons.append(SHAPE + f"{len(outside)} recorded answers changed outside what the task names: {sample}")
        print(f"  golden: {changed} recorded answers changed, {changed - len(outside)} of them named by the task")
    finally:
        for server in servers:
            server.stop()
        run(["git", "worktree", "remove", "--force", str(worktree)], head)
    return reasons


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument("--base", required=True, help="the commit the change starts from")
    parser.add_argument("--head", required=True, help="a checkout with the change committed")
    parser.add_argument("--allow", help="regular expression for golden requests the task may change")
    arguments = parser.parse_args()
    head = pathlib.Path(arguments.head).resolve()
    allow = re.compile(arguments.allow, re.IGNORECASE) if arguments.allow else None
    with socket.socket() as probe:
        if probe.connect_ex(("127.0.0.1", 27017)):
            print("FAIL: MongoDB is not running on 127.0.0.1:27017; start and seed it first")
            return 1
    reasons = file_reasons(head, arguments.base)
    code, output = run([pixi(), "run", "check"], head)
    if code:
        reasons.append(NOT_DONE + "pixi run check failed")
    code, output = run(["./se", "--doctor", "--offline"], head)
    if code:
        reasons.append(NOT_DONE + "./se --doctor reports a failed check")
    reasons += behaviour_reasons(head, arguments.base, allow)
    if reasons:
        print("FAIL: not light work")
        for reason in dict.fromkeys(reasons):  # once each, in order
            print(f"  - {reason}")
        return 1
    print("PASS: light work")
    return 0


if __name__ == "__main__":
    sys.exit(main())
