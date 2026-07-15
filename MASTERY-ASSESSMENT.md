# Java backend mastery assessment

Complete each checkpoint without reading its linked solution first. Record assumptions, evidence,
and tradeoffs; syntax recall alone does not pass.

## Scoring rubric

Score every scenario from 0–4:

| Score | Evidence |
|---:|---|
| 0 | Cannot identify the governing contract |
| 1 | Names a relevant API or slogan but cannot derive behavior |
| 2 | Explains the contract and proposes a plausible implementation |
| 3 | Also supplies a deterministic test and diagnoses the stated failure |
| 4 | Also compares alternatives, production impact, and compatibility |

A checkpoint passes with no score below 3 and an average of at least 3.5. The final assessment
also requires all automated capstone checks to pass.

## Checkpoint 1 — Core Java

1. A mutable object used as a `HashMap` key becomes unreachable by lookup. Diagnose and fix it.
2. Two equal-looking objects disagree under `==`, `equals`, and a set. State each contract.
3. Design a generic copy operation and justify its `? extends`/`? super` boundaries.
4. A stream pipeline reads a closed file only in production. Explain the lifetime error and test it.
5. Port a C++ RAII example containing a file and shared object graph without inventing destructors.

## Checkpoint 2 — Advanced Java engineering

1. A shutdown request leaves executor tasks running. Design and test cooperative cancellation.
2. Prove whether a publication is safe using a concrete happens-before edge rather than timing.
3. CPU rises while traffic stays flat. Order evidence from metrics, thread dumps, JFR, allocation
   profiles, and benchmarks; explain what each result would eliminate.
4. Diagnose a class that works in the IDE but fails from its packaged JAR.
5. Replace an inheritance-heavy pattern with records, sealing, composition, or a function and
   explain when the original pattern remains appropriate.

## Checkpoint 3 — Backend boundaries

1. Define an order DTO/domain/entity split and identify validation at each boundary.
2. Two requests confirm the same order. Preserve the state-machine invariant under concurrency.
3. An ORM list endpoint emits 501 SQL statements. Prove N+1 and select a use-case fetch plan.
4. Design stable pagination while concurrent inserts occur.
5. Map malformed input, missing data, forbidden ownership, stale state, and unexpected failure to
   an HTTP contract without exposing internals.

## Checkpoint 4 — Reliability and production

1. A client retries `POST /orders` after a timeout. Prevent duplicate orders and events.
2. A JWT-authenticated user reads another customer's order. Separate identity, authority, and
   object-level authorization.
3. PostgreSQL commits but Kafka is unavailable. Prove how the outbox recovers without dual writes.
4. An event is delivered twice. Make the application effect idempotent and state its transaction.
5. Rising p99 latency accompanies flat CPU. Choose telemetry, form falsifiable hypotheses, and
   avoid unmeasured JVM tuning.
6. Deploy a schema, HTTP, and event change tolerated by old clients and the previous application.

## Final capstone assessment

Run the module 22 milestones, then select four scenarios—one from each checkpoint—and demonstrate:

1. a spoken or written mental-model explanation;
2. a code or configuration change made from the contract;
3. a deterministic positive and negative test;
4. failure induction plus logs, metrics, trace, SQL, JFR, or thread/heap evidence;
5. recovery and rollback/roll-forward reasoning;
6. one rejected alternative with a concrete tradeoff.

Pass only when `./scripts/check-docs.sh`, `./scripts/check-curriculum.sh`,
`./scripts/verify-jdk-examples.sh`, and `./mvnw verify` succeed on JDK 25 and every selected
scenario scores at least 3. Do not average away a missing ability.
