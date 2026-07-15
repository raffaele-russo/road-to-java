#!/usr/bin/env bash
set -euo pipefail

ROOT=$(cd "$(dirname "$0")/.." && pwd)
cd "$ROOT"

JAVA=${JAVA:-java}
major=$($JAVA -version 2>&1 | sed -n '1s/.*version "\([0-9][0-9]*\).*/\1/p')
if [[ -z "$major" || "$major" -lt 25 ]]; then
  echo "JDK 25+ is required; '$JAVA -version' reported major version ${major:-unknown}." >&2
  exit 2
fi

examples=(
  00-cpp-vs-java/HelloComparison.java
  00-cpp-vs-java/Solution.java
  01-basics/Basics.java 01-basics/Solution.java
  02-oop/Oop.java 02-oop/Solution.java
  03-collections-generics/Collections.java 03-collections-generics/Generics.java
  03-collections-generics/Solution.java
  04-exceptions-io/Exceptions.java 04-exceptions-io/Solution.java
  05-functional-streams/Streams.java 05-functional-streams/Solution.java
  06-concurrency/Concurrency.java 06-concurrency/Solution.java
  07-jvm-memory/JvmInfo.java 07-jvm-memory/Solution.java
  08-modern-java/Modern.java 08-modern-java/Solution.java
  14-build-tooling/ToolingLab.java 14-build-tooling/Solution.java
  15-essential-apis/EssentialApis.java 15-essential-apis/Metadata.java
  15-essential-apis/Solution.java
  16-architecture/ArchitectureLab.java 16-architecture/Solution.java
  17-sql-persistence/PersistenceLab.java 17-sql-persistence/Solution.java
  18-spring-http/HttpLab.java 18-spring-http/Solution.java
  19-security/SecurityLab.java 19-security/Solution.java
  20-distributed-systems/ReliabilityLab.java 20-distributed-systems/Solution.java
  21-production/Solution.java
)

while IFS= read -r file; do examples+=("$file"); done < <(
  find 10-coding-problems -maxdepth 1 -name '*.java' \
    ! -name 'MergeIntervals.java' ! -name 'ProductExceptSelf.java' \
    ! -name 'KthLargestElement.java' ! -name 'MaxSubArray.java' | sort
)
while IFS= read -r file; do examples+=("$file"); done < <(find 10-coding-problems/solutions -name '*.java' | sort)
while IFS= read -r file; do examples+=("$file"); done < <(
  find 11-design-patterns -maxdepth 1 -name '*.java' ! -name 'Exercise.java' | sort
)

for file in "${examples[@]}"; do
  echo "[java] $file"
  "$JAVA" -ea "$file" >/dev/null
done

service_output=$(mktemp -d)
trap 'rm -rf "$service_output"' EXIT
"${JAVA%/java}/javac" --release 25 -d "$service_output" \
  15-essential-apis/service-loader/services/*.java
mkdir -p "$service_output/META-INF/services"
cp 15-essential-apis/service-loader/META-INF/services/services.GreetingProvider \
  "$service_output/META-INF/services/"
echo "[java] 15-essential-apis/service-loader/services/ServiceLoaderDemo.java"
"$JAVA" -ea -cp "$service_output" services.ServiceLoaderDemo >/dev/null

echo "Verified ${#examples[@]} solved JDK examples plus ServiceLoader discovery. Exercises remain learner-owned."
