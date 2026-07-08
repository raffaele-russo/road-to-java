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

## Modern file I/O (NIO.2, `java.nio.file`)

Prefer `Path`/`Files` over the legacy `java.io.File`:

```java
Path p = Path.of("data.txt");
Files.writeString(p, "hello");
String content = Files.readString(p);
List<String> lines = Files.readAllLines(p);
try (Stream<String> s = Files.lines(p)) { ... }   // lazy, for big files
```

## Run

```bash
java 04-exceptions-io/Exceptions.java
```
