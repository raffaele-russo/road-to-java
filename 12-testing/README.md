# 12 — Testing: JUnit 5 & Mockito

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

## What "good unit test" means here
- Tests one unit in isolation (mock its dependencies).
- Deterministic — no real network/filesystem/clock without seams.
- One logical assertion concept per test; name describes the scenario + expectation.
- Fast — a whole suite should run in seconds, not minutes.

## Run

```bash
mvn test
```
