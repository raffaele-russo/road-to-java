#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"

failed=0
required_phrases=('outcome|outcomes' 'mental model' 'failure' 'retrieval' 'exercise')

for module in {00..22}-*; do
  [[ -d "$module" ]] || continue
  readme="$module/README.md"
  if [[ ! -f "$readme" ]]; then
    echo "Missing module README: $readme" >&2
    failed=1
    continue
  fi
  for phrase in "${required_phrases[@]}"; do
    if ! grep -Eiq "$phrase" "$readme"; then
      echo "$readme lacks required curriculum evidence: $phrase" >&2
      failed=1
    fi
  done
done

for header in 'Competency' 'Theory' 'Demonstration' 'Practice' 'Automated proof' 'Assessment'; do
  grep -Fq "$header" CURRICULUM-MAP.md || {
    echo "CURRICULUM-MAP.md lacks column: $header" >&2
    failed=1
  }
done

[[ "$(tr -d '[:space:]' < .java-version)" == "25" ]] || {
  echo ".java-version must select the Java 25 curriculum baseline." >&2
  failed=1
}

for pom in 12-testing/pom.xml 13-spring-basics/pom.xml 21-production/pom.xml; do
  grep -Eq '<maven.compiler.release>25</maven.compiler.release>|<java.version>25</java.version>' "$pom" || {
    echo "$pom does not compile against the Java 25 baseline." >&2
    failed=1
  }
done
grep -Fq '<java.version>25</java.version>' 22-order-service/pom.xml || {
  echo "22-order-service/pom.xml does not compile against the Java 25 baseline." >&2
  failed=1
}

if grep -Fq -- '--enable-preview' scripts/verify-jdk-examples.sh; then
  echo "Required examples may not depend on preview features." >&2
  failed=1
fi

if rg -n 'TODO:|BUG:|does NOT compile|public class Exercise' \
    --glob 'Solution.java' --glob '*Solution.java' .; then
  echo "Reference solutions contain stale learner or failure markers." >&2
  failed=1
fi

if grep -En '\|[^|]*(TBD|TODO|missing)[^|]*\|' CURRICULUM-MAP.md; then
  echo "Required curriculum rows may not claim incomplete evidence." >&2
  failed=1
fi

exit "$failed"
