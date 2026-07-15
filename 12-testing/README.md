# 12 — Testing: JUnit 5 & Mockito

## Learning outcome and prerequisite

**Outcome:** Design deterministic unit, integration, boundary, and production-confidence tests.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

Unlike modules 00–11, this module needs a **build tool** (Maven) because JUnit and
Mockito aren't part of the JDK — this mirrors how testing actually works day-to-day in
a Java job, and is worth knowing cold for interviews ("how do you test a class with a
dependency?" is a near-universal question).

## Setup

```bash
brew install maven      # if you don't have it: mvn -version to check first
cd 12-testing
mvn test                # compiles src/main + src/test and runs everything
```

## Why testing matters more in Java interviews than in C++ ones

Java shops lean heavily on unit tests + DI (module 13) as the default architecture —
"write a test for this" or "how would you test this class" comes up constantly, often
more than raw algorithm questions. gtest/Catch2 experience transfers conceptually;
the syntax and idioms below are what's actually expected.

## JUnit 5 essentials — see `BankAccountTest.java`

| Annotation | Purpose |
|------------|---------|
| `@Test` | marks a test method |
| `@BeforeEach` / `@AfterEach` | runs before/after **every** test method (fresh fixture) |
| `@BeforeAll` / `@AfterAll` | runs **once** for the class (must be `static`) |
| `@DisplayName` | human-readable test name in reports |
| `@Nested` | groups related tests in an inner class, own lifecycle |
| `@ParameterizedTest` + `@ValueSource`/`@CsvSource` | run the same test body over many inputs |
| `@Disabled` | skip a test (leave a reason) |

### Assertions

```java
assertEquals(expected, actual);
assertEquals(expected, actual, delta);      // for doubles — never assert exact double equality
assertTrue(condition);
assertThrows(SomeException.class, () -> riskyCall());  // preferred over try/catch for exception tests
assertAll(() -> assertEquals(1, x), () -> assertEquals(2, y)); // grouped, reports all failures
```

### The AAA pattern (how to structure every test)

```java
@Test
void withdrawMoreThanBalanceThrows() {
    // Arrange
    account.deposit(50);
    // Act + Assert (often combined for exception tests)
    assertThrows(IllegalStateException.class, () -> account.withdraw(1000));
}
```

## Mockito essentials — see `NotificationServiceTest.java`

The build starts Mockito's instrumentation agent explicitly. Current JDKs warn that dynamic
self-attachment will be disabled; a test suite should not depend on that hidden runtime mutation.

Testing a class **in isolation** means faking its collaborators. That's what Mockito is for.

| Concept | Code |
|---------|------|
| Create a fake | `@Mock EmailSender sender;` + `@ExtendWith(MockitoExtension.class)` |
| Stub a return value | `when(sender.send(...)).thenReturn(true);` |
| Stub a failure | `when(x.call()).thenThrow(new RuntimeException());` |
| Verify a call happened | `verify(sender).send(...);` |
| Verify call count | `verify(sender, times(2)).send(...);` / `never()` |
| Verify no interaction | `verifyNoInteractions(sender);` |
| Inspect what was passed | `ArgumentCaptor<String> cap = ArgumentCaptor.forClass(String.class); verify(x).call(cap.capture());` |
| Argument matchers | `anyString()`, `eq(value)`, `any(SomeType.class)` — **can't mix raw values and matchers in one call** |

### Mock vs Spy

- **Mock**: a bare fake — every method returns `null`/`0`/`false` unless stubbed.
- **Spy**: wraps a **real** object — real methods run unless explicitly stubbed.
  Use sparingly; usually a sign the design could be cleaner (prefer mocks + DI).

```java
List<String> spyList = spy(new ArrayList<>());
spyList.add("real");              // really executes
when(spyList.size()).thenReturn(100); // this one call is stubbed
```

### Common Mockito mistakes (interview gotchas)
- Stubbing a method that's never called → `UnnecessaryStubbingException` (strict stubs).
- Mixing matchers and raw values in one call: `verify(x).f(eq("a"), "b")` — illegal, must
  matcher-ify all args or none.
- Mocking a class with `final` methods needs the inline mock maker (default since Mockito 5).
- Over-mocking: if you're mocking 6 collaborators, the class under test probably has too
  many responsibilities (violates SRP).

## Test doubles — the full taxonomy (not just "mock")

Interviewers who've read Meszaros/Fowler expect these five distinguished, not used as
synonyms:

| Double | Behavior |
|--------|----------|
| **Dummy** | passed to satisfy a signature, never actually used (e.g. `null`, or a no-op object) |
| **Stub** | returns canned answers to calls, no real logic (`when(x).thenReturn(...)`) |
| **Fake** | a working but simplified implementation (e.g. an in-memory `Map`-backed repo instead of a real DB) |
| **Spy** | wraps a real object; real methods run unless overridden |
| **Mock** | a stub that also lets you `verify()` it was called — asserts on *interactions*, not just state |

Mockito's `mock()` actually gives you a stub-or-mock depending on whether you `verify()`
it; the taxonomy is about intent, not a hard Mockito API distinction.

## `@TestInstance` — per-method vs per-class lifecycle

By default JUnit 5 creates a **new test instance per test method** (`PER_METHOD`) — so
instance fields can't leak state between tests without `@BeforeEach` resetting them.
`@TestInstance(Lifecycle.PER_CLASS)` shares one instance across all tests in the class,
which is what lets `@BeforeAll`/`@AfterAll` be non-static.

```java
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpensiveSetupTest {
    @BeforeAll void setUpOnce() { /* not static — needs PER_CLASS */ }
}
```

## Unit vs integration tests

- **Unit test**: one class, dependencies mocked/faked, no real I/O — fast, this module's
  focus.
- **Integration test**: exercises real collaborators (a real database, a real HTTP call,
  a real Spring context via `@SpringBootTest`) — slower, catches wiring bugs unit tests
  can't (module 13's `SpringContextTest` is effectively this).
A healthy suite is mostly unit tests with a thinner layer of integration tests on top
(the "test pyramid") — not the other way around.

## The executable testing pyramid

This module now demonstrates three boundary sizes:

| Test | Tool | What it proves |
|---|---|---|
| `BankAccountTest` / `NotificationServiceTest` | JUnit + Mockito | isolated policy and collaboration |
| `StatusClientTest` | WireMock | real HTTP serialization/status behavior without the internet |
| `PostgresContractTest` | Testcontainers | behavior of a real PostgreSQL engine; skipped when Docker is unavailable |
| module 22 controller tests | Spring MVC slice | routing, validation, security, and problem mapping |
| module 22 application tests | Spring Boot + Testcontainers | migrations, transactions, filters, and adapters together |

Test the smallest boundary that can prove the risk. Mock an owned collaborator in a unit test;
do not mock PostgreSQL when the risk is PostgreSQL SQL/locking behavior. Inject `Clock`, IDs,
executors, and HTTP endpoints so tests control nondeterminism. A concurrency test should use
latches/barriers and eventual assertions, never “sleep and hope.”

## TDD in one paragraph

Red (write a failing test for behavior that doesn't exist yet) → Green (write the
*minimum* code to pass it) → Refactor (clean up, tests still green). Interviewers rarely
require strict TDD in a live coding round, but "how would you approach writing this with
tests first" is a fair follow-up — know the cycle by name.

## Code coverage — a number, not a goal

Line/branch coverage (tools: JaCoCo) tells you what's *executed*, not what's *correctly
asserted* — 100% coverage with no assertions is worthless. Useful for finding completely
untested code paths, not as a quality target to chase for its own sake.

## What "good unit test" means here
- Tests one unit in isolation (mock its dependencies).
- Deterministic — no real network/filesystem/clock without seams.
- One logical assertion concept per test; name describes the scenario + expectation.
- Fast — a whole suite should run in seconds, not minutes.

## Practice exercise — write the tests from scratch

`src/main/java/discount/DiscountCalculator.java` is a small, already-implemented class
(loyalty-based discount pricing — a couple of branches and an exception). Open
[`src/exercise/java/discount/DiscountCalculatorExerciseTest.java`](src/exercise/java/discount/DiscountCalculatorExerciseTest.java):
every test method body currently calls `fail("TODO")`. Replace each with a real test. The
solved test remains in `src/test/java` so the normal repository build stays green:

1. `noDiscountUnderTwoYears` — loyalty `0` or `1` years → full price, no discount.
2. `tenPercentDiscountForTwoToFourYears` — a `@ParameterizedTest` over `{2, 3, 4}`.
3. `twentyPercentDiscountAtFiveYearsAndAbove` — a `@ParameterizedTest` over `{5, 10, 50}`.
4. `nonPositivePriceThrows` — `assertThrows` for `price <= 0`.

```bash
cd 12-testing && mvn -Pexercise test
```

## Run

```bash
mvn test
```

## Retrieval practice, hints, and solution

1. Which behavior belongs in a unit test versus an integration test?
2. How does a fake differ from a mock?
3. Why can high coverage still miss the contract?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: Solved examples run by default; use `mvn -Pexercise test` for the learner test.
