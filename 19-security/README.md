# 19 — Security for Java backends

## Outcomes and prerequisites

Build a threat-informed security boundary: authenticate identities, authorize each object/action,
handle credentials and secrets safely, configure browser defenses, and inspect dependencies.

## Mental model

Authentication answers “who presented this credential?” Authorization answers “may this
principal perform this action on this resource?” A valid JWT is not an authorization decision.
Security is enforced at boundaries and in business operations, with deny-by-default rules.

## Passwords, tokens, and secrets

- Store passwords with a dedicated adaptive password hash (Argon2id, bcrypt, or scrypt), unique
  salts, and a configurable work factor. Never encrypt passwords reversibly or use a fast digest.
- Validate JWT signature, allowed algorithm, issuer, audience, expiry/not-before, and required
  claims. Use short lifetimes and a rotation/revocation strategy appropriate to risk.
- OAuth2 defines delegated authorization flows; JWT is only one token format. The capstone is a
  resource server and does not implement its own authorization server.
- Load secrets from an external secret mechanism/environment, never source, images, logs, URLs,
  or default configuration. Rotate them and scope them minimally.

## Spring Security boundary

The capstone requires an authenticated `orders:write` authority for mutations and verifies
that a user can read only their own customer ID. Administrative operational details require
`ROLE_ADMIN`. Method-level authorization protects use cases as well as URL routing.

CSRF matters when browsers automatically attach credentials such as session cookies. A stateless
bearer-token API has a different threat model, but must still restrict CORS to explicit trusted
origins. CORS is a browser policy, not server-side authorization.

## Failure modes and production controls

- Object-level authorization missing: authenticated user reads another user's order.
- Mass assignment: inbound JSON sets status/owner fields; accept purpose-built command DTOs.
- Injection: concatenate SQL/shell/template input; use typed APIs and parameterization.
- Sensitive logs: redact tokens, credentials, personal data, and request bodies by default.
- Dependency risk: pin/scan dependencies, review advisories, update deliberately, produce an SBOM.
- Error oracle: return stable 401/403/404 policy without exposing existence or internal checks.

Use TLS, secure headers, request/body limits, rate controls at an appropriate edge, audit security-
relevant actions, and test both permitted and forbidden cases.

## Retrieval practice and exercise

Explain why verifying a JWT does not stop an insecure direct-object reference. In module 22,
test missing/invalid tokens, insufficient authority, wrong ownership, admin access, expired-token
behavior, and redaction. State whether each failure should be 401, 403, or deliberately 404.
