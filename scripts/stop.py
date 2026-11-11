"""Stop the server listening on a port (default 18080, where `pixi run serve` puts it).

    python scripts/stop.py [--port N]

It finds the process by the port it listens on, not by its command line: `pkill -f` with a
pattern also matches the shell that runs it, and kills that shell (FINDINGS.md, 2026-11-11).
It stops only a java process, and exits 1 if the port is still held after 30 seconds.
"""
import argparse
import os
import re
import signal
import subprocess
import sys
import time


def listeners(port):
    """(name, pid) of each process listening on the port, from `ss`."""
    output = subprocess.run(
        ["ss", "-ltnpH", f"sport = :{port}"], capture_output=True, text=True, check=True
    ).stdout
    return set(re.findall(r'\("([^"]+)",pid=(\d+)', output))


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawTextHelpFormatter)
    parser.add_argument("--port", type=int, default=18080)
    port = parser.parse_args().port
    found = listeners(port)
    if not found:
        print(f"nothing is listening on port {port}")
        return 0
    for name, pid in sorted(found):
        if name != "java":
            print(f"port {port} is held by {name} (pid {pid}), not a java server; not stopping it")
            return 1
        os.kill(int(pid), signal.SIGTERM)
        print(f"stopping java (pid {pid}) on port {port}")
    for _ in range(30):
        time.sleep(1)
        if not listeners(port):
            print("stopped")
            return 0
    print(f"port {port} is still held after 30 seconds")
    return 1


if __name__ == "__main__":
    sys.exit(main())
