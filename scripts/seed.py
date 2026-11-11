"""Load the 2014 SRD, and the 2024 files this API serves, from 5e-bits/5e-database into the local
MongoDB (run with: pixi run seed)."""
import json
import pathlib
import re
import subprocess
import tempfile

# Pinned to the release current at this code's last change, so a run today matches a run later.
COMMIT = "b207ac7b5377c0e6c674ff9fc266d44711dca730"
# Upstream stamps the load time; a fixed value keeps recorded responses comparable between runs.
UPDATED_AT = "2026-03-29T00:00:00.000Z"
CHECKOUT = pathlib.Path(".mongo/5e-database")
URI = "mongodb://127.0.0.1:27017/5e-database"
# The 2024 files served alongside the 2014 SRD, relative to upstream's src/.
FROM_2024 = ["2024/5e-SRD-Weapon-Mastery-Properties.json"]


def git(*args):
    subprocess.run(["git", "-C", str(CHECKOUT), *args], check=True)


def checkout():
    if not CHECKOUT.exists():
        subprocess.run(
            ["git", "clone", "-q", "https://github.com/5e-bits/5e-database.git", str(CHECKOUT)],
            check=True,
        )
    git("-c", "advice.detachedHead=false", "checkout", "-q", COMMIT)


def collection_name(path):
    # Upstream's rule, from its scripts/dbUtils.ts: 2014/5e-SRD-Damage-Types.json -> 2014-damage-types.
    # The prefix is the year's directory, which a 2014 file under en/ does not name.
    year = "2024" if path.parent.name == "2024" else "2014"
    return year + "-" + re.sub(r"[\s_]+", "-", re.sub(r"^5e-SRD-|\.json$", "", path.name).lower())


def load(path):
    records = json.loads(path.read_text(encoding="utf-8"))
    for record in records:
        record["updated_at"] = UPDATED_AT
    with tempfile.NamedTemporaryFile("w", suffix=".json", encoding="utf-8") as out:
        json.dump(records, out)
        out.flush()
        subprocess.run(
            ["mongoimport", "--quiet", "--uri", URI, "--drop", "--jsonArray", "--maintainInsertionOrder",
             "--collection", collection_name(path), "--file", out.name],
            check=True,
        )
    print(f"{collection_name(path)}: {len(records)}")


def main():
    checkout()
    root = CHECKOUT / "src" / "2014"
    if (root / "en").is_dir():
        root = root / "en"
    files = sorted(root.glob("5e-SRD-*.json"))
    if not files:
        raise SystemExit(f"no SRD files under {root}")
    for path in files + [CHECKOUT / "src" / name for name in FROM_2024]:
        load(path)


if __name__ == "__main__":
    main()
