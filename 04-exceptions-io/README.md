# 04 — Exceptions & I/O

## The exception hierarchy

```
Throwable
 ├── Error              JVM problems — DON'T catch (OutOfMemoryError, StackOverflowError)
 └── Exception
      ├── RuntimeException   UNCHECKED — programming bugs
      │    ├── NullPointerException
      │    ├── IllegalArgumentException / IllegalStateException
      │    ├── IndexOutOfBoundsException
      │    └── ClassCastException, ArithmeticException, ...
      └── (everything else)  CHECKED — recoverable conditions
           ├── IOException
           ├── SQLException
           └── ...
```

## Checked vs unchecked — the C++ dev's new concept

C++ has no compile-time exception checking. Java does:

- **Checked exceptions** (subclass `Exception` but not `RuntimeException`): the compiler
  *forces* you to either `catch` them or declare `throws` on your method. A contract.
- **Unchecked exceptions** (`RuntimeException`, `Error`): no compiler enforcement.

Rule of thumb:
- Throw **unchecked** for programming errors (bad args, illegal state) — the caller
  can't reasonably recover.
- Throw **checked** for expected, recoverable failures (file not found, network down).
- Don't catch `Error`.

```java
// declaring
void readFile(Path p) throws IOException { ... }

// catching + multi-catch + finally
try {
    risky();
} catch (IOException | SQLException e) {   // multi-catch
    log(e);
    throw new AppException("failed", e);   // wrap, preserve cause
} finally {
    cleanup();                             // always runs
}
```

## try-with-resources (Java's RAII substitute)

Any `AutoCloseable` is auto-closed at block exit, in reverse order, even on exception.
This is the closest thing to C++ RAII / smart pointers for deterministic cleanup.

```java
try (var in = Files.newBufferedReader(src);
     var out = Files.newBufferedWriter(dst)) {
    // use in/out
}   // out.close() then in.close() — automatically
```

Implement it yourself:
```java
class Resource implements AutoCloseable {
    Resource() { System.out.println("open"); }
    public void close() { System.out.println("close"); }  // called automatically
}
```

## Best practices

- **Never swallow**: an empty `catch {}` block hides bugs.
- **Preserve the cause**: `new MyException("msg", e)` chains the stack trace.
- **Don't catch broad `Exception`** unless at a top-level boundary.
- **Don't use exceptions for control flow** — they're expensive (stack capture).
- Prefer `Optional` (module 05) over throwing for "not found" cases.
- Custom exceptions: extend `RuntimeException` (unchecked) unless you truly want the
  caller forced to handle it.

## Designing custom exceptions

```java
class InsufficientFundsException extends RuntimeException {          // unchecked: caller
    private final double shortfall;                                   // usually can't recover
    InsufficientFundsException(double shortfall) {
        super("short by " + shortfall);
        this.shortfall = shortfall;
    }
    double shortfall() { return shortfall; }
}
```
Rules of thumb for an interview: extend `RuntimeException` unless you have a specific
reason to force every caller to handle it; add fields for structured data (not just a
message string) when callers might need to react programmatically; always call
`super(message, cause)` to preserve the chain when wrapping.

## The `finally` + `return` gotcha

A `return` (or `throw`) inside `finally` **silently discards** whatever the `try`/`catch`
was about to return or throw — a classic "what does this print" interview trap:

```java
static int f() {
    try {
        return 1;
    } finally {
        return 2;             // this wins — f() returns 2, the try's return 1 is lost
    }
}
```
Never `return` from `finally`. It's legal, and that's exactly the trap.

## Suppressed exceptions in try-with-resources

If both the try block **and** `close()` throw, the `close()` exception isn't lost — it's
attached as a **suppressed** exception on the primary one, retrievable via
`getSuppressed()`. (Before Java 7, the `close()` exception would silently mask the real one.)

```java
try (AutoCloseable r = () -> { throw new IllegalStateException("close failed"); }) {
    throw new RuntimeException("body failed");
} catch (Exception e) {
    e.getMessage();          // "body failed" — the primary exception
    e.getSuppressed();       // [IllegalStateException: close failed] — not lost
}
```

## Rethrowing and exception translation

```java
void loadConfig(Path p) {
    try {
        Files.readString(p);
    } catch (IOException e) {
        throw new ConfigException("could not load " + p, e);  // wrap: preserve the cause, add context
    }
}
```
Catching `IOException` (checked) and rethrowing as an unchecked `ConfigException` is a
common pattern at a layer boundary — the lower layer's checked exception shouldn't leak
into a service interface that doesn't want to force every caller to handle I/O details.

## `Scanner` vs `BufferedReader`

`Scanner` parses (tokenizes, type-converts) but is slower; `BufferedReader` just reads
lines fast. For competitive-programming-style stdin parsing, `BufferedReader` +
manual `split`/`parseInt` is the faster idiom; `Scanner` is more convenient for quick scripts.

```java
Scanner sc = new Scanner(System.in);
int n = sc.nextInt();                 // convenient, but noticeably slower on large input

BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
int n2 = Integer.parseInt(br.readLine().trim());   // faster, more typing
```

## Modern file I/O (NIO.2, `java.nio.file`)

Prefer `Path`/`Files` over the legacy `java.io.File`:

```java
Path p = Path.of("data.txt");
Files.writeString(p, "hello");
String content = Files.readString(p);
List<String> lines = Files.readAllLines(p);
try (Stream<String> s = Files.lines(p)) { ... }   // lazy, for big files
```

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). Build a tiny bank-transfer validator:

1. Define `InsufficientFundsException` (unchecked, carries a `shortfall` field) and
   `InvalidAmountException` (unchecked) — both with a message + cause constructor.
2. Implement `withdraw(double balance, double amount)`: throws `InvalidAmountException`
   for `amount <= 0`, `InsufficientFundsException` (with the correct shortfall) if
   `amount > balance`, otherwise returns the new balance.
3. Implement `parseAmounts(List<String> lines)` using a **suppressed-exceptions-aware**
   accumulation: try to parse every line as a `double`; if any fail, throw one
   `NumberFormatException` whose message lists all bad lines, with each individual
   parse failure attached via `addSuppressed`, instead of stopping at the first bad line.

```bash
java -ea 04-exceptions-io/Exercise.java
```

## Run

```bash
java 04-exceptions-io/Exceptions.java
```
