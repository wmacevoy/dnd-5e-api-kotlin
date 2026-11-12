"""Record what the running API answers, or compare a running API against that record.

    python scripts/golden.py record  [--base URL]    # rewrites golden/manifest.tsv
    python scripts/golden.py compare [--base URL]    # exit 1 if a rest or mcp answer differs

It talks to a server over HTTP and knows nothing of the code, so any implementation can be held to
the same record. The manifest stores status codes and hashes, not responses, which keeps SRD text
out of this repository; full bodies go to .golden/, which is ignored and can be recorded again.
The "schema" group (the OpenAPI document) is reported but cannot fail a compare: how much of it a
design keeps is one of the things being measured.

A case whose recorded answer names a class of this code (an error's message) is written with
"unchecked" in place of its hash, and only its status is compared (EXPERIMENT.md, rule 3). A
re-record keeps that mark, since another implementation's text will not name those classes.
"""
import argparse
import hashlib
import http.client
import json
import pathlib
import queue
import sys
import threading
import urllib.error
import urllib.parse
import urllib.request

MANIFEST = pathlib.Path("golden/manifest.tsv")
BODIES = pathlib.Path(".golden/bodies")
ACTUAL = pathlib.Path(".golden/actual")
PAGING = {"page", "size", "sort"}
MISSING_VALUE = "__no_such_value__"
UNCHECKED = "unchecked"
CODE_NAMES = "com.thejaxonhill"
LARGEST_PAGE = 2000  # Spring's limit on a requested page size

# REST path, MCP list tool, MCP get-by-index tool. The tool names are not derivable from the paths.
RESOURCES = [
    ("ability-scores", "getAbilityScores", "getAbilityScore"),
    ("alignments", "getAlignments", "getAlignment"),
    ("backgrounds", "getBackgrounds", "getBackground"),
    ("classes", "getClasses", "getCharacterClass"),
    ("conditions", "getConditions", "getCondition"),
    ("damage-types", "getDamageTypes", "getDamageType"),
    ("equipment", "getAllEquipment", "getEquipment"),
    ("equipment-categories", "getEquipmentCategories", "getEquipmentCategory"),
    ("feats", "getFeats", "getFeat"),
    ("features", "getFeatures", "getFeature"),
    ("languages", "getLanguages", "getLanguage"),
    ("levels", "getLevels", "getLevel"),
    ("magic-items", "getMagicItems", "getMagicItem"),
    ("magic-schools", "getMagicSchools", "getMagicSchool"),
    ("monsters", "getMonsters", "getMonster"),
    ("proficiencies", "getProficiencies", "getProficiency"),
    ("races", "getRaces", "getRace"),
    ("rule-sections", "getRuleSections", "getRuleSection"),
    ("rules", "getRules", "getRule"),
    ("skills", "getSkills", "getSkill"),
    ("spells", "getSpells", "getSpell"),
    ("subclasses", "getSubclasses", "getSubclass"),
    ("subraces", "getSubraces", "getSubrace"),
    ("traits", "getTraits", "getTrait"),
    ("weapon-properties", "getWeaponProperties", "getWeaponProperty"),
]


def http_get(base, path):
    try:
        with urllib.request.urlopen(base + path, timeout=60) as response:
            return response.status, response.read().decode("utf-8")
    except urllib.error.HTTPError as error:
        return error.code, error.read().decode("utf-8")


def parsed(text):
    """JSON if the text is JSON, otherwise the text; a body's key order is not part of the contract."""
    try:
        return json.loads(text)
    except (TypeError, ValueError):
        return text


class McpSession:
    def __init__(self, base):
        self.base = base
        url = urllib.parse.urlparse(base)
        self.stream = http.client.HTTPConnection(url.hostname, url.port, timeout=120)
        self.stream.request("GET", "/sse", headers={"Accept": "text/event-stream"})
        self.response = self.stream.getresponse()
        self.endpoint = queue.Queue()
        self.messages = queue.Queue()
        threading.Thread(target=self._read, daemon=True).start()
        self.path = self.endpoint.get(timeout=30)
        self.next_id = 0
        self.call("initialize", {
            "protocolVersion": "2024-11-05",
            "capabilities": {},
            "clientInfo": {"name": "golden", "version": "1"},
        })
        self._post({"jsonrpc": "2.0", "method": "notifications/initialized"})

    def _read(self):
        event = None
        for raw in self.response:
            line = raw.decode("utf-8").rstrip("\r\n")
            if line.startswith("event:"):
                event = line[6:].strip()
            elif line.startswith("data:"):
                target = self.endpoint if event == "endpoint" else self.messages
                target.put(line[5:].strip())

    def _post(self, message):
        request = urllib.request.Request(
            self.base + self.path,
            json.dumps(message).encode("utf-8"),
            {"Content-Type": "application/json"},
            method="POST",
        )
        with urllib.request.urlopen(request, timeout=60):
            pass

    def call(self, method, params):
        self.next_id += 1
        self._post({"jsonrpc": "2.0", "id": self.next_id, "method": method, "params": params})
        return json.loads(self.messages.get(timeout=60))


def run_case(base, session, kind, request):
    """Returns (status, canonical value) for one recorded request."""
    if kind == "http":
        status, text = http_get(base, request)
        value = parsed(text)
        if isinstance(value, dict) and "openapi" in value:
            # springdoc writes the address it was asked on, so the same code on another port differed.
            value.pop("servers", None)
        return status, value
    if kind == "mcp-tools":
        tools = session.call("tools/list", {})["result"]["tools"]
        return 200, sorted(tools, key=lambda tool: tool["name"])
    tool, arguments = request.split(" ", 1)
    reply = session.call("tools/call", {"name": tool, "arguments": json.loads(arguments)})
    if "error" in reply:
        return reply["error"].get("code", -1), reply["error"]
    result = reply["result"]
    content = [{**part, "text": parsed(part.get("text"))} for part in result["content"]]
    return (1 if result.get("isError") else 0), content


def canonical(value):
    return json.dumps(value, sort_keys=True, ensure_ascii=False, separators=(",", ":"))


def digest(value):
    return hashlib.sha256(canonical(value).encode("utf-8")).hexdigest()[:16]


def query(path, **params):
    return f"{path}?{urllib.parse.urlencode(params)}"


def rest_cases(base, path, parameters):
    url = f"/api/{path}"
    everything = query(url, size=LARGEST_PAGE)
    items = parsed(http_get(base, everything)[1])["content"]
    cases = [
        url,
        everything,
        query(url, page=1, size=3),
        query(url, size=5, sort="name,desc"),
        query(url, size="abc"),
        query(url, page=-1),
        query(url, size=0),
        query(url, sort="nonexistent"),
        f"{url}/not-found",
    ]
    filters = {}
    for name in sorted(parameters - PAGING):
        value = next((item[name] for item in items if isinstance(item.get(name), (str, int))), None)
        if value is not None:
            filters[name] = value
            cases.append(query(url, **{name: value, "size": LARGEST_PAGE}))
            cases.append(query(url, **{name: MISSING_VALUE}))
    cases.extend(f"{url}/{item['index']}" for item in items)
    return cases, [item["index"] for item in items], filters


def mcp_cases(list_tool, get_tool, indexes, filters, properties):
    def call(tool, arguments):
        return f"{tool} {json.dumps(arguments, sort_keys=True)}"

    # List tools come in two shapes: pageNumber and pageSize, or a page object and a filter object.
    cases = [
        call(list_tool, {"pageNumber": 0, "pageSize": LARGEST_PAGE}),
        call(list_tool, {"pageNumber": 1, "pageSize": 3}),
        call(list_tool, {"page": {"page": 0, "size": LARGEST_PAGE}}),
        call(list_tool, {"page": {"page": 1, "size": 3}}),
        call(list_tool, {}),
        call(get_tool, {"index": "not-found"}),
    ]
    example = next((name for name in properties if "xample" in name), None)
    if example:
        page = {"page": 0, "size": LARGEST_PAGE}
        for name, value in filters.items():
            cases.append(call(list_tool, {"page": page, example: {name: value}}))
    cases.extend(call(get_tool, {"index": index}) for index in indexes)
    return cases


def all_cases(base, session):
    """Every request to record, as (group, kind, request). Discovered once, at record time."""
    documented = parsed(http_get(base, "/v3/api-docs")[1])
    tools = {tool["name"]: tool for tool in session.call("tools/list", {})["result"]["tools"]}
    cases = [("schema", "http", "/v3/api-docs"), ("mcp", "mcp-tools", "tools/list")]
    for path, list_tool, get_tool in RESOURCES:
        operation = documented["paths"][f"/api/{path}"]["get"]
        parameters = {parameter["name"] for parameter in operation.get("parameters", [])}
        requests, indexes, filters = rest_cases(base, path, parameters)
        cases.extend(("rest", "http", request) for request in requests)
        properties = tools[list_tool]["inputSchema"]["properties"]
        for request in mcp_cases(list_tool, get_tool, indexes, filters, properties):
            cases.append(("mcp", "mcp-call", request))
    search = "/api/equipment/search"
    for params in [{}, {"query": "sword"}, {"query": "SWORD"}, {"query": ""}, {"query": MISSING_VALUE},
                   {"query": "sword", "page": 1, "size": 3}, {"query": "sword", "sort": "name,desc"}]:
        cases.append(("rest", "http", query(search, **params) if params else search))
    cases.append(("rest", "http", query("/api/equipment", query="sword", size=1)))
    cases.append(("rest", "http", "/no-such-route"))
    return cases


def save_body(directory, sha, value):
    directory.mkdir(parents=True, exist_ok=True)
    text = json.dumps(value, sort_keys=True, ensure_ascii=False, indent=1)
    (directory / f"{sha}.json").write_text(text, encoding="utf-8")


def unchecked_before():
    if not MANIFEST.exists():
        return set()
    lines = (line.split("\t", 4) for line in MANIFEST.read_text(encoding="utf-8").splitlines())
    return {request for group, status, sha, kind, request in lines if sha == UNCHECKED}


def record(base):
    session = McpSession(base)
    cases = all_cases(base, session)
    unchecked = unchecked_before()
    MANIFEST.parent.mkdir(parents=True, exist_ok=True)
    with MANIFEST.open("w", encoding="utf-8") as out:
        for group, kind, request in cases:
            status, value = run_case(base, session, kind, request)
            sha = digest(value)
            save_body(BODIES, sha, value)
            if request in unchecked or CODE_NAMES in canonical(value):
                sha = UNCHECKED
            out.write(f"{group}\t{status}\t{sha}\t{kind}\t{request}\n")
    print(f"recorded {len(cases)} cases in {MANIFEST}")


def first_difference(expected, actual, path="$"):
    if type(expected) is not type(actual):
        return f"{path}: {type(expected).__name__} became {type(actual).__name__}"
    if isinstance(expected, dict):
        for key in sorted(set(expected) | set(actual)):
            if key not in expected or key not in actual:
                return f"{path}.{key}: {'added' if key in actual else 'removed'}"
            found = first_difference(expected[key], actual[key], f"{path}.{key}")
            if found:
                return found
    elif isinstance(expected, list):
        if len(expected) != len(actual):
            return f"{path}: length {len(expected)} became {len(actual)}"
        for position, (left, right) in enumerate(zip(expected, actual)):
            found = first_difference(left, right, f"{path}[{position}]")
            if found:
                return found
    elif expected != actual:
        return f"{path}: value differs"
    return None


def explain(sha, value):
    body = BODIES / f"{sha}.json"
    if not body.exists():
        return "no recorded body here; run record on the base commit to see the difference"
    return first_difference(json.loads(body.read_text(encoding="utf-8")), value) or "same value"


def compare(base):
    session = McpSession(base)
    totals = {}
    differing = {}
    for line in MANIFEST.read_text(encoding="utf-8").splitlines():
        group, status, sha, kind, request = line.split("\t", 4)
        actual_status, value = run_case(base, session, kind, request)
        actual_sha = digest(value)
        totals[group] = totals.get(group, 0) + 1
        if str(actual_status) != status or (sha != UNCHECKED and actual_sha != sha):
            save_body(ACTUAL, actual_sha, value)
            detail = "only its status is held" if sha == UNCHECKED else explain(sha, value)
            note = f"status {status} became {actual_status}; {detail}"
            differing.setdefault(group, []).append(f"{request}  ({note})")
    for group, count in sorted(totals.items()):
        found = differing.get(group, [])
        note = "  (measured, not held: a refactor may change it)" if group == "schema" and found else ""
        print(f"{group}: {count - len(found)} of {count} match{note}")
        for entry in found[:20]:
            print(f"  {entry}")
        if len(found) > 20:
            print(f"  ... and {len(found) - 20} more")
    required = sum(len(found) for group, found in differing.items() if group != "schema")
    print("GOLDEN MATCH" if not required else f"GOLDEN MISMATCH: {required} differ")
    return 1 if required else 0


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument("action", choices=["record", "compare"])
    parser.add_argument("--base", default="http://127.0.0.1:18080")
    arguments = parser.parse_args()
    if arguments.action == "record":
        record(arguments.base)
        return 0
    return compare(arguments.base)


if __name__ == "__main__":
    sys.exit(main())
