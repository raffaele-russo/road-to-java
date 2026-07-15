#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"
failed=0

# rg emits file and link target as two fields; spaces in Markdown link targets are intentionally unsupported.
while IFS=: read -r file target; do
  target=${target%%#*}
  [[ -z "$target" || "$target" == http://* || "$target" == https://* || "$target" == mailto:* ]] && continue
  [[ -e "$(dirname "$file")/$target" ]] || { echo "Broken local link in $file: $target" >&2; failed=1; }
done < <(rg --glob '*.md' -o --replace '$1' '\[[^]]+\]\(([^ )]+)\)' --with-filename | sed 's/:\([^:]*\)$/|\1/' | tr '|' ':')

exit "$failed"
