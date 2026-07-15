# Java backend mastery assessment

Do this after the capstone. Record assumptions, evidence, and tradeoffs; syntax recall alone
does not pass.

1. A mutable object used as a `HashMap` key becomes unreachable by lookup. Diagnose and fix it.
2. A stream pipeline reads a closed file only in production. Explain the lifetime error and test it.
3. A shutdown request leaves executor tasks running. Design cooperative cancellation.
4. CPU rises while request volume stays flat. Choose evidence from metrics, thread dumps, JFR,
   allocation profiles, and benchmarks; explain the order.
5. Two requests confirm the same order. Preserve the state-machine invariant under concurrency.
6. A client retries `POST /orders` after a timeout. Prevent duplicate orders and duplicate events.
7. An ORM list endpoint emits 501 SQL statements. Identify N+1 and select an appropriate fetch plan.
8. A JWT-authenticated user reads another customer's order. Separate authentication from object-level authorization.
9. PostgreSQL commits but Kafka is unavailable. Explain how the outbox recovers without dual writes.
10. Deploy a schema and API change that old clients and the previous application version can tolerate.

Pass criteria: the capstone checks succeed and each answer identifies the violated contract,
uses an appropriate diagnostic or test, proposes a correction, and states at least one tradeoff.
