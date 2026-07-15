# 20 — Reliable distributed backend fundamentals

## Outcomes and prerequisites

Design bounded remote work, retry safely, make commands idempotent, keep database state and events
consistent, and consume at-least-once messages without duplicating effects.

## Mental model

A remote call can succeed while its response is lost. Therefore “timeout” means “unknown outcome,”
not “nothing happened.” Networks introduce latency, reordering, duplication, partitions, and
independent failure. Exactly-once end-to-end delivery is normally achieved as an application
effect through idempotency and durable state, not a transport promise.

## Timeouts, retries, and load

Every remote operation needs a deadline. Connect and response timeouts solve different waits.
Retry classified transient failures with exponential backoff, jitter, a cap, and an overall
budget. Do not multiply retries across layers. Retries can amplify overload; concurrency limits
and circuit breaking protect the dependency and caller.

## Idempotency

For order creation, store `(principal, idempotency-key, request-hash, response/order-id)` under a
unique constraint in the same transaction as the order. A replay with the same hash returns the
original result; reuse with different input returns conflict. Validate keys and expire records
only according to a documented client retry window.

## Transactional outbox

Writing PostgreSQL and Kafka directly creates a dual-write gap. Instead:

```text
business transaction: order change + outbox row COMMIT
publisher: claim row → publish event → mark published
consumer: insert event-id in processed table + apply effect COMMIT
```

The publisher can publish twice if it crashes after broker acknowledgement; the consumer's unique
event ID makes the effect idempotent. Preserve ordering only where the domain requires it, normally
by aggregate key. Use schema-versioned events and additive evolution.

## Caching and consistency

Cache-aside reduces reads but introduces staleness and invalidation. Define source of truth, key,
TTL, maximum size, eviction behavior, and failure policy. Never cache authorization decisions or
sensitive values without a threat-specific design. A cache failure should not silently change a
business invariant.

## Delivery semantics and failure modes

At-most-once may lose work; at-least-once may duplicate; “exactly once” is scoped to a system and
must be verified for the application effect. Dead-letter queues quarantine failures but do not
resolve them: include diagnosis, replay, ordering, retention, and alerting procedures.

## Retrieval practice and exercise

Walk through crashes before commit, after commit/before publish, after publish/before marking, and
during consumer commit. For each, prove how the capstone recovers and whether it can duplicate an
event or effect. Then distinguish an idempotency key from optimistic locking: they solve request
replay and concurrent state mutation respectively.

## Progressive example and mastery evidence

[`ReliabilityLab.java`](ReliabilityLab.java) isolates a bounded retry and an idempotent consumer
effect. It retries two transient failures and delivers one event twice. This proves only local
contracts; the capstone outbox demonstrates durable composition and crash recovery.

```bash
java -ea 20-distributed-systems/ReliabilityLab.java
java -ea 20-distributed-systems/Solution.java
```

Implement [`Exercise.java`](Exercise.java). Hints: validate the budget, remember the last failure,
and use the boolean returned by `Set.add`. Production needs a durable unique key in the same
transaction as the effect plus retry classification, backoff, jitter, deadlines, and load limits.
