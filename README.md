# Road to Java — professional Java for experienced C++ developers

This is a self-study curriculum for becoming productive with modern Java: first the
language and JVM, then engineering practices, then a production-style backend. It targets
**Java 25 LTS**. Notes marked **17/21 compatibility** explain what changes in older codebases.

“Mastery” here means that you can explain a concept, use it, test it, and diagnose its
failure modes. It does not mean memorising every JDK class. Java 25 is the current LTS;
Spring Boot 4.1 supports Java 17 through 26.

- [Oracle Java support roadmap](https://www.oracle.com/uk/java/technologies/java-se-support-roadmap.html)
- [Spring Boot system requirements](https://docs.spring.io/spring-boot/system-requirements.html)
- [Java SE 25 API](https://docs.oracle.com/en/java/javase/25/docs/api/)

## Start here

Install JDK 25 and run:

```bash
java -version
java 00-cpp-vs-java/HelloComparison.java
./scripts/verify-jdk-examples.sh
./mvnw verify
```

Both `java -version` and `./mvnw -version` must report a JDK in the supported 25.x range
(the build also accepts 26 for forward-compatibility checks). This machine previously had
different JDKs on the direct Java and Maven paths; set `JAVA_HOME`/`PATH` consistently if the
two commands disagree. `.java-version` lets compatible JDK managers select 25 automatically.

Modules 00–11 remain single-file programs so that syntax never hides behind build-tool
configuration. Dependency-based modules use Maven. Every learning module follows
[the learning contract](LEARNING-CONTRACT.md): outcomes, mental model, examples, failure
modes, choices, production implications, retrieval practice, exercise, hints, and solution.

## Track 1 — Core Java

| # | Module | Outcome |
|---|---|---|
| 00 | [C++ vs Java](00-cpp-vs-java/README.md) | Replace C++ ownership, value, compilation, and inheritance assumptions |
| 01 | [Basics](01-basics/README.md) | Types, Unicode strings, arrays, numeric rules, and control flow |
| 02 | [OOP](02-oop/README.md) | Model identity, equality, immutability, interfaces, and initialization |
| 03 | [Collections & generics](03-collections-generics/README.md) | Choose collections and design type-safe generic APIs |
| 04 | [Exceptions & I/O](04-exceptions-io/README.md) | Design failure boundaries and manage resources safely |
| 05 | [Functional Java & streams](05-functional-streams/README.md) | Build lazy pipelines and collectors without obscuring intent |
| 08 | [Modern Java](08-modern-java/README.md) | Use records, sealed types, and exhaustive pattern matching |
| 10 | [Coding problems](10-coding-problems/README.md) | Express common algorithms idiomatically and analyse complexity |

## Track 2 — Advanced Java engineering

| # | Module | Outcome |
|---|---|---|
| 06 | [Concurrency](06-concurrency/README.md) | Reason from happens-before; implement cancellation and safe concurrency |
| 07 | [JVM & memory](07-jvm-memory/README.md) | Diagnose allocation, GC, class-loading, JIT, and runtime failures |
| 11 | [Design patterns](11-design-patterns/README.md) | Recognise patterns and prefer simpler Java language features when possible |
| 12 | [Testing](12-testing/README.md) | Build a deterministic unit/integration testing pyramid |
| 14 | [Build & tooling](14-build-tooling/README.md) | Build, package, document, analyse, and debug Java projects |
| 15 | [Essential APIs](15-essential-apis/README.md) | Correctly handle time, money, text, IDs, HTTP, JSON, and metadata |
| 16 | [Architecture](16-architecture/README.md) | Design explicit boundaries and a maintainable modular monolith |
| 09 | [Interview and mastery questions](09-interview-qa/README.md) | Retrieve and communicate the mental models under pressure |

## Track 3 — Production backend

| # | Module | Outcome |
|---|---|---|
| 13 | [Spring fundamentals](13-spring-basics/README.md) | Understand dependency injection before relying on Boot magic |
| 17 | [SQL & persistence](17-sql-persistence/README.md) | Use transactions, JDBC, migrations, JPA, locking, and pagination safely |
| 18 | [Spring Boot & HTTP](18-spring-http/README.md) | Design validated, evolvable REST APIs with consistent errors |
| 19 | [Security](19-security/README.md) | Apply authentication, authorization, secret, browser, and supply-chain controls |
| 20 | [Distributed systems](20-distributed-systems/README.md) | Design timeouts, retries, idempotency, caching, and message delivery |
| 21 | [Production engineering](21-production/README.md) | Observe, profile, containerize, operate, and safely deliver services |
| 22 | [Order-service capstone](22-order-service/README.md) | Integrate the curriculum in one production-style modular service |

Specializations intentionally outside the required path: Android, JavaFX, reactive
programming, Kubernetes, cloud-vendor products, and microservice orchestration. Learn
those after the backend foundations, when a real project demands them.

## Coverage matrix

`L` learn, `D` runnable demonstration, `P` practice, `T` automated test, `C` capstone.

| Capability | Module | L | D | P | T | C |
|---|---:|:---:|:---:|:---:|:---:|:---:|
| Language, identity, equality, immutability | 00–02 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Collections, generics, streams | 03, 05 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Failures, files, resources | 04 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Concurrency and Java Memory Model | 06 | ✓ | ✓ | ✓ | ✓ | ✓ |
| JVM, GC, JIT, diagnostics | 07, 21 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Modern language features | 08 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Algorithms and complexity | 10 | ✓ | ✓ | ✓ | ✓ | — |
| Testing pyramid | 12 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Build, modules, static analysis | 14 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Time, money, Unicode, HTTP, metadata | 15 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Architecture and API design | 11, 16 | ✓ | ✓ | ✓ | ✓ | ✓ |
| SQL, transactions, ORM | 17 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Spring, HTTP, validation | 13, 18 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Security | 19 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Reliability, messaging, idempotency | 20 | ✓ | ✓ | ✓ | ✓ | ✓ |
| Logs, metrics, traces, profiling, delivery | 21 | ✓ | ✓ | ✓ | ✓ | ✓ |

## Mastery checklist

You are ready to call the required path complete when you can:

- Explain references, equality, generic variance, stream laziness, and exception policy
  without using memorized slogans alone.
- Choose a collection and justify its contract, complexity, ordering, and concurrency behavior.
- Derive whether concurrent code is correct using happens-before, interruption, and ownership.
- Use a thread dump, heap evidence, JFR recording, and measurements to investigate a problem.
- Design and test a transactional HTTP operation with validation, idempotency, authorization,
  optimistic locking, consistent errors, logs, metrics, and traces.
- Complete the capstone scenarios and the retrieval questions without reading the answers first.

Use [MASTERY-ASSESSMENT.md](MASTERY-ASSESSMENT.md) for the final scenario-based assessment.
