# 18 — Spring Boot and HTTP API design

## Outcomes and prerequisites

Explain Boot auto-configuration, design an HTTP contract, validate untrusted input, map domain
failures to stable problem responses, and test the boundary without testing framework internals.

## Mental model

Spring Boot chooses configuration based on the classpath, properties, and missing/present beans.
It does not change dependency injection: constructor dependencies and explicit boundaries from
module 13 remain the model. Use the condition evaluation report when “magic” is surprising.

HTTP is a distributed protocol with caches, retries, intermediaries, partial failure, and clients
released on different schedules. Status codes, headers, media types, and idempotency are part of
the API—not controller decoration.

## Contract used by the capstone

| Operation | Success | Important failures |
|---|---|---|
| `POST /api/orders` | `201`, `Location`, order body | 400 validation, 409 idempotency conflict |
| `GET /api/orders/{id}` | `200` | 404 |
| `GET /api/orders` | `200` page | 400 invalid filter/page |
| `POST .../{id}/confirm` | `200` | 404, 409 invalid/stale transition |
| `POST .../{id}/cancel` | `200` | 404, 409 invalid/stale transition |

Use request/response records, Bean Validation at the inbound boundary, and an exception handler
that returns `application/problem+json` with stable `type`, `title`, `status`, `detail`, and
request-path fields. Do not leak stack traces, SQL, or security decisions.

## REST and compatibility rules

- `GET` is safe and idempotent. `PUT` and `DELETE` are idempotent by semantics; `POST` needs an
  application idempotency mechanism when clients will retry it.
- Validate syntax at the adapter and business invariants in the domain. Validation annotations
  do not replace database constraints.
- Page with a stable sort and bounded size. Avoid returning database entities.
- Adding optional response fields is usually compatible; changing meaning/type/removing fields is not.
- Negotiate JSON explicitly and document examples, errors, authentication, and retry behavior.

## Outbound HTTP

Set connection and request timeouts. Retry only classified transient failures, only within a
budget, and only when the operation is idempotent. Propagate correlation/trace context, bound
payloads, validate TLS, and translate transport failures at the adapter boundary.

## Testing and retrieval practice

Use plain unit tests for domain/use cases, MVC slice tests for mapping/validation/security, and
full integration tests for serialization, database, migrations, and filters. Explain why an
HTTP 500 is not an acceptable mapping for an invalid state transition, then trace a capstone
request from JSON through transaction and back to a problem response.
