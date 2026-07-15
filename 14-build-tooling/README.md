# 14 — Build, packaging, modules, and developer tooling

## Outcomes and prerequisites

After modules 00–12, you should be able to explain how source becomes a runnable artifact,
diagnose classpath problems, read a Maven build, create a JAR, and choose evidence-producing
quality tools. Run `../mvnw verify` from the repository root.

## Mental model and C++ bridge

`javac` produces JVM bytecode in `.class` files. A JAR is normally a ZIP of classes and
resources; it is not the equivalent of native machine code. The classpath answers “where
can the runtime find named classes?” The Java module path additionally answers “which
packages does a named module export and which modules does it require?” There is no C++
header/linker split, but dependency resolution and runtime class loading can still fail.

```text
source → javac --release 25 → class files → test/package → JAR → java/class loader
                       Maven coordinates resolve compile/test/runtime dependencies ↑
```

## Maven in practice

The lifecycle is ordered: `validate → compile → test → package → verify → install → deploy`.
Calling `verify` also performs earlier phases. Dependencies have scopes: `compile` is part
of main compilation/runtime, `runtime` is absent during compilation, `test` is test-only,
and `provided` must exist in the target environment. Prefer a BOM or parent dependency
management to independently pinning a web of related libraries.

```bash
./mvnw -pl 12-testing test          # one module
./mvnw -pl 22-order-service verify # capstone, including integration checks
jar --list --file app.jar
javap -c -p SomeClass.class         # bytecode and members
jdeps --recursive app.jar           # module/package dependencies
```

`--release 25` controls language, bytecode, and the exposed JDK API together. Setting only
`source` and `target` can accidentally compile against APIs missing from the target runtime.

## Packages, classpath, and JPMS

A package is a namespace and an access boundary. Its directory convention mirrors the
qualified name. A module descriptor makes dependencies and exports explicit:

```java
module com.example.orders {
    requires java.net.http;
    exports com.example.orders.api;
}
```

Use JPMS for a deliberate module boundary or custom runtime image, not merely because a
project has several packages. Most Spring services use Maven modules plus package-level
architecture rather than strict JPMS modules.

## Choice guide and failure modes

| Need | Tool | Common failure |
|---|---|---|
| Repeatable dependency build | Maven Wrapper | Running an arbitrary globally installed Maven |
| API docs | Javadoc | Comments repeat implementation instead of contracts |
| Compile-time bug patterns | compiler warnings/SpotBugs | Treating every warning as stylistic |
| Formatting | formatter | Mixing formatting changes with behavioral changes |
| Dependency inspection | `dependency:tree` | Version mediation selects an unexpected transitive version |
| Runtime dependency diagnosis | `jdeps`, `-verbose:class` | “Works in IDE” classpath differs from packaged app |

Never put secrets in a POM, committed properties file, command history, or generated build
report. Build output is disposable; source and migration files are not.

## Debugging and documentation

Use breakpoints to inspect state, conditional breakpoints to isolate a case, and exception
breakpoints to stop where a failure originates. Prefer a minimized test over a long manual
debugging session. Public Javadoc documents preconditions, results, exceptions, mutability,
thread safety, and ownership—not the obvious sequence of statements.

## Retrieval practice

1. Why is `--release` safer than only `source`/`target`?
2. What is the difference between dependency management and a dependency declaration?
3. When does a classpath problem become a module-path problem?
4. Why should `verify` be the CI entry point rather than a collection of IDE actions?

Answers: `--release` also restricts available APIs; management supplies versions but does
not add the dependency; JPMS adds readability/export rules; `verify` is repeatable and
machine-enforced. Exercise: inspect this root build with `help:effective-pom`,
`dependency:tree`, `jar`, `javap`, and `jdeps`, then explain one observation from each.

## Progressive example and exercise

Predict the result before running [`ToolingLab.java`](ToolingLab.java). The minimal case converts
a binary class name to its JAR entry, the composition case asks the runtime which JPMS module owns
`HttpClient`, and the boundary case demonstrates why a Java 25 class cannot run on Java 21. This
replaces the C++ assumption that a successful native link settles runtime compatibility.

```bash
java -ea 14-build-tooling/ToolingLab.java
java -ea 14-build-tooling/Solution.java
```

Expected: both print a single `passed` line. Implement [`Exercise.java`](Exercise.java) first.
Hints: distinguish a binary name from a path; class-file release must not exceed runtime release;
then replace dots with slashes and compare the releases. Reference: [`Solution.java`](Solution.java).

## 17/21 compatibility

The build targets 25. To support 17 or 21, set `maven.compiler.release` to that version and
replace newer language/API use; running a JDK 25 compiler alone does not make code portable.
