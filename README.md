# Road to Java — from C++ to a Java interview

A self-study repo for an experienced **C++ developer** moving to **Java**, structured
from easier to harder. Every module has a `README.md` with **theory + a C++
comparison** and **runnable example files** you can execute on their own.

Built and tested against **Java 21 (LTS)**. Modules 12–13 additionally use **Maven**
(`brew install maven`) since JUnit/Mockito/Spring aren't part of the JDK.

## How to use this repo

Modules 00–11 are pure JDK: each `.java` example has a `main` method and is a
*single-file program*. With Java 11+ you can run any file directly without compiling first:

```bash
java 01-basics/Basics.java
```

Files under `10-coding-problems/` use `assert` — enable assertions with `-ea`:

```bash
java -ea 10-coding-problems/TwoSum.java
```

To compile the classic way:

```bash
javac 01-basics/Basics.java && java -cp 01-basics Basics
```

Modules 12–13 are Maven projects (JUnit/Mockito/Spring need real dependencies):

```bash
cd 12-testing && mvn test
cd 13-spring-basics && mvn test && mvn compile exec:java
```

Work top to bottom. Read the module `README.md`, then run and tweak the examples.
Don't just read — change values, break things, re-run.

## Roadmap

| # | Module | What you learn | Priority for interviews |
|---|--------|----------------|--------------------------|
| 00 | [C++ vs Java](00-cpp-vs-java/README.md) | Mental-model shifts, side-by-side cheatsheet | ⭐ read first |
| 01 | [Basics](01-basics/README.md) | Types, references, strings, arrays, control flow | ⭐⭐⭐ |
| 02 | [OOP](02-oop/README.md) | Classes, interfaces, inheritance, `equals`/`hashCode` | ⭐⭐⭐ |
| 03 | [Collections & Generics](03-collections-generics/README.md) | List/Map/Set, generics, wildcards | ⭐⭐⭐ |
| 04 | [Exceptions & I/O](04-exceptions-io/README.md) | Checked vs unchecked, try-with-resources | ⭐⭐ |
| 05 | [Functional & Streams](05-functional-streams/README.md) | Lambdas, method refs, Stream API, Optional | ⭐⭐⭐ |
| 06 | [Concurrency](06-concurrency/README.md) | Threads, executors, synchronization, memory model | ⭐⭐⭐ |
| 07 | [JVM & Memory](07-jvm-memory/README.md) | GC, heap/stack, class loading, JIT | ⭐⭐ |
| 08 | [Modern Java](08-modern-java/README.md) | Records, sealed types, pattern matching, `var` | ⭐⭐ |
| 09 | [Interview Q&A](09-interview-qa/README.md) | Rapid-fire questions with answers | ⭐⭐⭐ |
| 10 | [Coding problems](10-coding-problems/README.md) | 13 problems: HashMap, BFS, heaps, DP, backtracking, sliding window, two pointers | ⭐⭐⭐ |
| 11 | [Design Patterns](11-design-patterns/README.md) | Singleton, Factory, Builder, Adapter, Decorator, Strategy, Observer — the Java-specific angle on each | ⭐⭐⭐ |
| 12 | [Testing](12-testing/README.md) | JUnit 5 (assertions, lifecycle, parameterized) + Mockito (mocks, stubs, verify) | ⭐⭐⭐ |
| 13 | [Spring Basics](13-spring-basics/README.md) | DI/IoC, bean scopes/lifecycle, annotations — real runnable Spring project | ⭐⭐⭐ |

## The 10 things that trip up C++ devs

1. **Everything is a reference** (except primitives). No pointers, no `->`, no `*`.
2. **No manual memory management.** The garbage collector owns object lifetime. No `delete`, no destructors — use `try-with-resources` for cleanup.
3. **No `const`.** `final` means "cannot reassign the variable", *not* "cannot mutate the object". There is no `const`-correctness.
4. **No free functions.** Everything lives in a class. `static` methods are the closest thing.
5. **Generics are erased** at runtime (type erasure), unlike C++ templates which are monomorphized. No `List<int>` — only `List<Integer>`.
6. **Checked exceptions** must be declared or caught — a compile-time contract with no C++ equivalent.
7. **Single inheritance** of classes; multiple inheritance only via interfaces (which can have default methods).
8. **`==` compares references** for objects; use `.equals()` for value equality. Huge source of bugs.
9. **No operator overloading**, no stack-allocated objects, no RAII, no header/`.cpp` split.
10. **Autoboxing** silently converts `int` ↔ `Integer`, which can bite you on `==` and performance.

## Quick start

```bash
java -version          # confirm 21+
java 00-cpp-vs-java/HelloComparison.java
```
