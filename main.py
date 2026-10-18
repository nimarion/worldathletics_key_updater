#!/usr/bin/env python3
import http.client
from pathlib import Path
import re
import sys

HOST = "worldathletics.org"
PAGE_PATH = "/athletes/-/14193865"
HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
        "(KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
    ),
    "Connection": "keep-alive",
}

SCRIPT_SRC_PATTERN = re.compile(r'src="(/_next/static/[^"]+\.js)"')
GRAPHQL_BLOCK_PATTERN = re.compile(r"(?:^|[,{])graphql:\{([^}]+)\}")
ENDPOINT_PATTERN = re.compile(r'(?:^|[,{])endpoint:"([^"]+)"')
API_KEY_PATTERN = re.compile(r'(?:^|[,{])apiKey:"([^"]+)"')


def fetch(conn: http.client.HTTPSConnection, path: str) -> str:
    for _ in range(3):
        try:
            conn.request("GET", path, headers=HEADERS)
            resp = conn.getresponse()
            body = resp.read().decode("utf-8", errors="ignore")
            if 200 <= resp.status < 300:
                return body
        except Exception:
            conn.close()
    raise RuntimeError(f"Failed to fetch {path}")


def main() -> int:
    conn = http.client.HTTPSConnection(HOST, timeout=10)
    try:
        html = fetch(conn, PAGE_PATH)
        for script_path in SCRIPT_SRC_PATTERN.findall(html):
            js = fetch(conn, script_path)
            for block in GRAPHQL_BLOCK_PATTERN.findall(js):
                endpoint_match = ENDPOINT_PATTERN.search(block)
                api_key_match = API_KEY_PATTERN.search(block)
                if endpoint_match and api_key_match:
                    endpoint = endpoint_match.group(1)
                    api_key = api_key_match.group(1)

                    stellate_path = Path("stellate.ts")
                    content = stellate_path.read_text(encoding="utf-8")
                    content = content.replace("wa-api-key", api_key)
                    content = content.replace("wa-api-endpoint", endpoint)
                    stellate_path.write_text(content, encoding="utf-8")

                    print(f"{endpoint}:{api_key}")
                    return 0
    finally:
        conn.close()
    return 1


if __name__ == "__main__":
    sys.exit(main())
