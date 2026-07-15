# 22 — Order-service capstone

This capstone connects domain modelling, decimal money, transactions, HTTP, validation,
authorization, idempotency, optimistic locking, an outbox, Kafka-compatible messaging,
observability, containers, and integration testing in one modular monolith.

## Outcomes and prerequisites

Complete modules 00–21. You should be able to trace a request, state the invariant at every
boundary, induce each documented failure, and identify the evidence that proves recovery.

## Mental model and C++ bridge

Treat the service as one durable state machine with protocol, transaction, domain, and delivery
boundaries. A successful Java method return is insufficient evidence: the HTTP response may be
lost, the database may reject commit, or event publication may repeat. The transferable C++ habit
is explicit invariants and ownership; the new habit is reasoning across independently failing
process and network boundaries.

## Architecture

```text
JWT/HTTP → api → application transaction → domain
                    ↓              ↓
               JPA/Postgres   transactional outbox → broker → idempotent consumer
```

Packages point inward: `api` maps protocol data, `application` owns use cases/transactions,
`domain` owns invariants, and persistence/messaging are adapters. It is one deployable process;
there are no fake internal microservices.

## Run

Requirements: JDK 25, Maven, and Docker for PostgreSQL/broker and container tests.

```bash
docker compose -f 22-order-service/compose.yaml up -d
./mvnw -pl 22-order-service spring-boot:run
./mvnw -pl 22-order-service verify
```

Outbox publishing and the demonstration consumer are off by default so a learner can debug the
HTTP/database stages independently. Enable them with `OUTBOX_ENABLED=true` and
`CONSUMER_ENABLED=true`. Use [api-examples.http](api-examples.http) after supplying a development
JWT. The committed symmetric secret is explicitly local-only; production must inject a strong
secret or configure an issuer/JWK source.

Activate deterministic local seed data and the broker workers with
`--spring.profiles.active=local`. The complete machine-readable API contract is
[`openapi.yaml`](openapi.yaml); update it and its boundary tests together whenever the API changes.

## HTTP contract

| Method and path | Authorization | Contract |
|---|---|---|
| `POST /api/orders` | `orders:write` | Requires `Idempotency-Key`; returns 201 + Location |
| `GET /api/orders/{id}` | `orders:read`, owner/admin | Returns one order or 404 |
| `GET /api/orders` | `orders:read` | Owner's orders, optional status, stable bounded page |
| `POST /api/orders/{id}/confirm` | `orders:write`, owner/admin | PENDING → CONFIRMED |
| `POST /api/orders/{id}/cancel` | `orders:write`, owner/admin | PENDING → CANCELLED |

Invalid input is 400, missing orders 404, stale/invalid transitions or idempotency misuse 409,
unauthenticated requests 401, and forbidden operations 403. Application errors use
`application/problem+json`; security-filter errors use Spring Security's boundary.

## Domain and persistence contracts

- IDs are UUIDs, timestamps are UTC `Instant`s, and monetary arithmetic is scaled `BigDecimal`.
- An order has at least one item and one currency. Quantity is positive.
- Only `PENDING` can transition to `CONFIRMED` or `CANCELLED`; terminal states cannot transition.
- `@Version` detects stale concurrent updates. The API translates that conflict to 409.
- Database checks and foreign keys repeat universal invariants at the durable boundary.
- Create stores order, idempotency result, and `OrderCreated` outbox row in one transaction.

The idempotency scope is `(JWT subject, Idempotency-Key)`. Replaying the same logical command
returns the original order. Reusing the key for a different command is a conflict. The tutorial
hash uses the immutable command representation; a public API should define canonical JSON or a
documented semantic hash before independent clients depend on it.

## Outbox delivery and failure analysis

The publisher reads unpublished rows, sends an event keyed by order ID, then records publication.
A crash between send and mark can duplicate delivery. The consumer records the event UUID under
a primary key in the same transaction as its effect, making repeats harmless. In a production
publisher, add row claiming (`SKIP LOCKED`/leases), retry classification, backoff, metrics, and
dead-letter/replay operations before running multiple publisher instances.

## Milestones

1. Run `OrderTest`; explain money and state invariants.
2. Run the Flyway/Testcontainers test; inspect schema constraints and indexes.
3. Start HTTP with outbox disabled; exercise validation, ownership, transitions, and pagination.
4. Repeat/reuse an idempotency key and inspect the database transaction.
5. Enable the broker path; stop it before/after commit and prove eventual publication.
6. Generate concurrent confirms; observe one success and one optimistic conflict.
7. Inspect structured logs/health/metrics, record JFR, and test graceful SIGTERM.
8. Complete [the mastery assessment](../MASTERY-ASSESSMENT.md).

## Hints and reference decisions

Hint 1: locate the invariant owner before changing a controller. Hint 2: locate the transaction
before adding a remote call. Hint 3: enumerate crash points before claiming reliable delivery.
The checked-in implementation is the reference solution; improve it only with a failing test or
an operational requirement that demonstrates the need.

## Retrieval practice and failure injection

Before reading code, predict the durable state and client-visible response for each boundary:

1. validation fails before a transaction starts;
2. the same idempotency key repeats with equal and unequal commands;
3. two transactions confirm the same version;
4. PostgreSQL commits while the broker is unavailable;
5. the publisher sends and crashes before marking the row;
6. the consumer receives one event twice;
7. a valid token names another customer's order;
8. SIGTERM arrives while work is in flight.

For every answer name the invariant, transaction or protocol boundary, expected log/metric/test,
recovery behavior, and one tradeoff. The milestone commands are the applied exercise; the checked-in
implementation and tests are the reference solution.
