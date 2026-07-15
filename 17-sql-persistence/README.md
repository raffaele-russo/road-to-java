# 17 — SQL, JDBC, transactions, and ORM

## Outcomes and prerequisites

Model relational data, reason about transaction isolation, use migrations, avoid ORM query
traps, and select concurrency controls. Apply this material in module 22.

## Mental model and C++ bridge

The database is a concurrent durable state machine, not a remote object collection. SQL works
on sets; a transaction groups statements into an atomic consistency boundary. An ORM maps
representations and tracks entity changes—it does not remove SQL, indexes, locks, or latency.

## Relational design and SQL

Use primary keys for identity, foreign keys for references, `NOT NULL` and `CHECK` constraints
for universal invariants, and unique constraints for race-safe uniqueness. Normalize facts
until update anomalies disappear; denormalize only for a measured read need. An index speeds
selected access paths but costs writes and space. Verify with the database query plan.

Parameterized JDBC prevents injection and keeps code/data separate:

```java
try (PreparedStatement statement = connection.prepareStatement(
        "select id, status from orders where id = ?")) {
    statement.setObject(1, id);
    try (ResultSet rows = statement.executeQuery()) { /* map while resources are open */ }
}
```

Use a bounded connection pool. A connection is scarce; close it promptly so it returns to the
pool. Never hold a database transaction open while waiting on a remote service.

## Transactions and concurrency

Atomicity is all-or-nothing, consistency preserves declared invariants, isolation controls
interleavings, and durability preserves committed work. Know dirty reads, non-repeatable reads,
phantoms, and lost updates, but diagnose from an actual invariant rather than selecting the
highest isolation reflexively.

Optimistic locking uses a version and rejects a stale update—appropriate when contention is
low and retry/rejection is acceptable. Pessimistic locking blocks competing access—appropriate
for short, genuinely contended critical sections. Unique constraints and conditional updates
often express invariants more directly.

## Flyway and compatible delivery

Migration files are immutable history. Use additive-expand/migrate/contract changes so the old
and new application versions can coexist during deployment. Do not rename/drop a column in the
same release that introduces its replacement. Seed only deterministic local/test data.

## JPA/Hibernate choice guide

| Concern | Rule |
|---|---|
| Entity identity | Database identity; equality must not depend on a mutable field |
| Fetching | Default to explicit use-case fetch plans; never rely on open-session-in-view |
| N+1 | Detect with SQL counts; use join/entity graph/batch for that query |
| Pagination | Stable ordering plus a unique tiebreaker; keyset for deep/large feeds |
| Writes | Transaction in application service; flush may fail before commit |
| DTOs | Map at the boundary; do not serialize lazy entities |

`LAZY` is not a query plan. Cascades describe persistence propagation, not domain ownership.
Bulk updates bypass the persistence context and may leave tracked entities stale.

## Retrieval practice and exercise

Explain how two concurrent confirmations could cause a lost update, how `@Version` detects it,
and which HTTP response the application should return. Inspect module 22's migration, constraints,
repository queries, transaction boundary, pagination ordering, and Testcontainers tests.

Hints: begin with the invariant; identify the competing statements; decide whether rejection,
retry, or blocking matches the use case; make the database enforce what it can.
