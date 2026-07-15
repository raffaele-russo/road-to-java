# 16 — Architecture, domain modelling, and code quality

## Outcomes and prerequisites

Design code whose dependencies point toward business policy, make mutation and nullability
explicit, and enforce module boundaries with tests. The capstone applies this module.

## Mental model and C++ bridge

Architecture is the shape of change: which code must change together and which details can be
replaced independently. Java interfaces are useful at volatile boundaries, not automatically
for every class. Compared with C++, favor composition and immutable values; GC removes manual
deallocation but not ownership, aliasing, or lifecycle design.

## Domain and boundary rules

- Put invariants beside the state they protect; do not rely on controllers to remember them.
- Represent concepts with types (`Money`, `OrderStatus`) instead of primitives with comments.
- Use constructor injection and make required dependencies non-null.
- Return immutable snapshots or defensive copies; never expose mutable internal collections.
- Use `Optional` for an absent return value, not fields, parameters, or every nullable detail.
- Keep transport DTOs, persistence entities, and domain concepts separate when their reasons
  to change differ. Avoid mechanical layers that add no boundary.

The capstone is a **modular monolith**: one deployable process, feature-oriented packages,
explicit internal APIs, one database, and no network calls between its own modules. This gives
strong boundaries without distributed-system failure modes.

```text
HTTP adapter → application use case → domain model
                         ↓ ports
             persistence / messaging adapters
```

## SOLID without slogans

| Principle | Concrete design question |
|---|---|
| Single responsibility | Which actor/reason causes this code to change? |
| Open/closed | Can a new policy be composed without editing stable orchestration? |
| Liskov substitution | Does every implementation preserve the interface contract? |
| Interface segregation | Is a caller forced to depend on operations it cannot use? |
| Dependency inversion | Does policy name the capability while infrastructure implements it? |

Duplication is cheaper than the wrong abstraction. Extract after the common concept and its
variation are understood. Patterns from module 11 are vocabulary, not mandatory structure.

## Failure modes and choice guide

- Anemic domain: setters permit invalid transitions; move behavior into the aggregate.
- “Util” dumping ground: behavior has no owner; name the concept or boundary.
- Interface per class: indirection without substitutability; keep concrete code concrete.
- Shared mutable singleton: hidden coupling and race conditions; pass explicit immutable state.
- Leaky repository: application code depends on ORM sessions; return domain-facing results.
- Catch-all service: transaction, HTTP, SQL, mapping, and policy change together; split by boundary.

Use records for immutable transparent data; ordinary classes for identity and guarded state;
sealed hierarchies for closed alternatives; strategies for open policy variation.

## Retrieval practice and exercise

Sketch the order-service dependency direction. Identify the owner of state transitions, the
transaction boundary, DTO mapping boundary, and outbox port. Then explain why extracting each
as a network service would make correctness harder. Validate the design against the package
rules and tests in module 22.

## Progressive example and mastery evidence

[`ArchitectureLab.java`](ArchitectureLab.java) begins with an invariant-owning object, composes it
with an immutable collection snapshot, then deliberately repeats a terminal transition. GC does
not remove aliasing or state ownership; Java uses defensive copies where C++ might use values.

```bash
java -ea 16-architecture/ArchitectureLab.java
java -ea 16-architecture/Solution.java
```

Implement [`Exercise.java`](Exercise.java). Hints: name who owns the list and state; copy at the
boundary and enumerate legal transitions; use `List.copyOf` and fail closed. Module 22 verifies
the realistic package-dependency and transaction composition.
