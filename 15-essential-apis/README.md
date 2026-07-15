# 15 — Essential APIs, correctness types, and metaprogramming

## Outcomes and prerequisites

Use the correct representation for money, time, text, IDs, HTTP, and metadata. Run:

```bash
java EssentialApis.java
java Metadata.java
```

## Mental model and C++ bridge

Types should make illegal operations difficult. `BigDecimal` models decimal arithmetic;
`Instant` models a point on the UTC timeline; `LocalDate` models a calendar date without a
zone; `UUID` models an opaque identifier. A `String` is UTF-16 code units, not a byte array
and not necessarily a sequence of user-perceived characters.

## Choice guide

| Problem | Prefer | Avoid |
|---|---|---|
| Money | `BigDecimal` with explicit scale/rounding | `double` equality and constructors |
| Machine timestamp | `Instant` | Server-local `LocalDateTime` |
| Human appointment | local date/time + `ZoneId` | Hard-coded numeric offset |
| Measure duration | `System.nanoTime()` | Subtracting wall-clock timestamps |
| Stable opaque ID | `UUID` | Sequential IDs exposed without thought |
| Text matching | compiled `Pattern` | Recompiling regex inside a hot loop |
| JDK HTTP | `HttpClient` with timeout | Unbounded wait and swallowed interruption |
| JSON boundary | explicit DTO + Jackson | Persisting/returning domain objects directly |

`new BigDecimal("0.1")` is exact; `new BigDecimal(0.1)` preserves the binary floating-point
approximation. Equality includes scale (`1.0` is not `.equals(1.00)`), while `compareTo`
compares numerical value. Define scale and rounding at the business boundary.

Time-zone rules change and daylight-saving transitions create gaps and overlaps. Persist an
`Instant`; retain a `ZoneId` when the human zone is part of the business meaning. Inject a
`Clock` so tests do not race the wall clock.

Unicode operations such as `length()` and `charAt()` operate on UTF-16 code units. Use
`codePoints()` when Unicode code points matter; grapheme clusters require a text-boundary
library/`BreakIterator` rather than assuming one visible symbol equals one `char`.

## HTTP and JSON boundaries

Set connect and request timeouts, inspect status before deserializing, preserve interruption,
and bound response sizes. JSON is an external contract: use DTO records, explicit validation,
and a compatibility policy for added/removed fields. Never deserialize untrusted polymorphic
type names.

## Reflection, annotations, proxies, and services

Reflection inspects runtime metadata and invokes members dynamically. An annotation is data;
it does nothing until a compiler, framework, or your code interprets it. Dynamic proxies can
intercept interface calls and explain features such as transactions, but self-invocation does
not cross a proxy boundary. `ServiceLoader` provides JDK-native provider discovery.

Annotation processing runs at compile time and generates source/resources; reflection runs at
runtime. Prefer normal calls and explicit composition until metadata removes genuine repetition.

## Failure modes and production implications

- Default charset/locale/time zone makes behavior machine-dependent: pass them explicitly.
- Regexes can exhibit catastrophic backtracking: constrain input and pattern structure.
- Reflection weakens refactoring and native-image reachability: isolate and test it.
- HTTP retries without idempotency can duplicate state changes.
- Logging full JSON DTOs can expose secrets or personal data.

## Retrieval practice and exercise

Explain why an order uses `BigDecimal`, `Instant`, `UUID`, and DTO records. Then run the two
examples, add a DST-overlap case, a supplementary Unicode code point, and an annotation whose
runtime retention you can observe. Reference behavior is asserted in each example.

## Progressive example and mastery evidence

[`EssentialApis.java`](EssentialApis.java) supplies realistic money/time/text composition and
[`Metadata.java`](Metadata.java) covers runtime metadata. The focused exercise adds three boundary
cases: decimal input from text, a supplementary code point plus combining mark, and the duplicated
local time during the Paris DST overlap. `String.length()` counts UTF-16 units; even code-point
count is not a user-perceived grapheme count.

```bash
java -ea 15-essential-apis/EssentialApis.java
java -ea 15-essential-apis/Metadata.java
java -ea 15-essential-apis/Solution.java
JAVA=/path/to/jdk-25/bin/java ./scripts/verify-jdk-examples.sh # includes ServiceLoader resources
```

Start with [`Exercise.java`](Exercise.java). Hints: construct decimals from strings; ask `String`
for code points; ask `ZoneRules` for valid offsets. The capstone proves HTTP/JSON composition.
The service-loader fixture includes the required `META-INF/services` registration; merely
implementing the provider interface does not make a provider discoverable.
