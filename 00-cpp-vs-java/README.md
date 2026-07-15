# 00 — C++ vs Java: the mental-model shift

## Learning outcome and prerequisite

**Outcome:** Predict Java reference, lifetime, parameter-passing, and compilation behavior before writing code.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

Read this before anything else. Java's syntax looks like C++, which is a trap: the
*semantics* underneath are different.

## Side-by-side cheatsheet

| Concept | C++ | Java |
|---------|-----|------|
| Memory | manual (`new`/`delete`), RAII, smart pointers | GC-managed, no `delete` |
| Object on stack | `Foo f;` creates an object | `Foo f;` is just a null reference; `Foo f = new Foo();` |
| Passing objects | by value / ref / pointer | always by value **of a reference** (like passing a pointer by value) |
| Pointers | `*`, `->`, `&`, pointer arithmetic | none; references only, no arithmetic |
| Null | `nullptr` | `null`; dereferencing → `NullPointerException` |
| `const` | pervasive const-correctness | none; `final` = non-reassignable binding only |
| Destructor | `~Foo()` deterministic | none; `finalize()` deprecated; use `AutoCloseable` + try-with-resources |
| Header/impl split | `.h` / `.cpp` | single `.java`, no forward declarations |
| Free functions | allowed | none; use `static` methods in a class |
| Multiple inheritance | classes | interfaces only |
| Templates | compile-time monomorphization | generics via **type erasure** (runtime-erased) |
| Operator overloading | yes | no (except built-in `+` for `String`) |
| Value/copy semantics | copy ctor, `=` deep/shallow | reference assignment; use `clone()`/copy ctor explicitly |
| Preprocessor | `#define`, `#include` | none; `import`, no macros |
| Compilation target | native machine code | bytecode → JVM (JIT at runtime) |
| Undefined behavior | abundant | none by design; errors are exceptions |
| Integer overflow | UB (signed) | well-defined wraparound (two's complement) |
| `enum` | integer constants | full-fledged classes with methods |
| Build | make/cmake | Maven/Gradle |

## The reference model (the #1 thing to internalize)

In C++:
```cpp
std::string a = "hi";
std::string b = a;   // deep copy — b is independent
b += "!";            // a is still "hi"
```

In Java, objects are never copied on assignment:
```java
StringBuilder a = new StringBuilder("hi");
StringBuilder b = a;   // b and a point to the SAME object
b.append("!");         // a is now "hi!" too
```

`String` happens to be immutable, so it *feels* like value semantics — but that's
because you can't mutate it, not because it's copied.

## Passing to methods

Java is **always pass-by-value**. For objects, the *value being copied is the
reference*. So:
- You **can** mutate the pointed-to object inside a method (visible to caller).
- You **cannot** make the caller's variable point to a different object.

```java
void f(int[] arr, int x) {
    arr[0] = 99;   // caller sees this — same array object
    x = 99;        // caller does NOT see this — primitive copy
    arr = new int[]{1,2,3}; // caller does NOT see this — reassigning local copy
}
```

## `final` is not `const`

```java
final List<String> list = new ArrayList<>();
list.add("still mutable"); // OK — final only stops REASSIGNMENT
// list = new ArrayList<>(); // compile error
```
For real immutability, use `List.of(...)`, `Collections.unmodifiableList`, or records.

## No destructors — deterministic cleanup

C++ RAII has no direct equivalent. For resources (files, sockets), implement
`AutoCloseable` and use **try-with-resources** (see module 04):

```java
try (var reader = Files.newBufferedReader(path)) {
    // use reader
} // reader.close() called automatically, even on exception
```

## Compilation & tooling — no linker, no headers

| | C++ | Java |
|-|-----|------|
| Compile | `g++ -c a.cpp b.cpp` per translation unit | `javac` compiles the whole graph it can see |
| Link | separate link step (`ld`), produces one binary | no linker — `.class` files are loaded individually by the JVM at runtime |
| Declare before use | forward declarations / headers | not needed — the compiler resolves symbols across `.java` files in the same compilation |
| Dependency management | manual (`-I`, `-L`, vcpkg/conan) | Maven/Gradle resolve a dependency *graph* with transitive versions |
| Static vs dynamic linking | a real, explicit choice | irrelevant — `.class` files are always loaded dynamically by a classloader (module 07), on demand, the first time a class is used |
| ABI stability | fragile across compilers/versions | bytecode is the stable contract; a `.class` compiled years ago still runs on a new JVM |

This is why Java has no `#include` guards, no "forward declare this class", and no
one-definition-rule linker errors — the JVM resolves and loads classes lazily, one at a
time, the first time each is actually referenced.

## The diamond problem — why Java allows multiple interface inheritance but not multiple class inheritance

C++ multiple inheritance can create a genuine diamond ambiguity (two base classes each
define `foo()`, which one wins?) that requires virtual inheritance to resolve. Java
sidesteps this for **state** by disallowing multiple class inheritance entirely, but
still allows multiple interface inheritance because interfaces (mostly) don't carry
state. If two interfaces provide *conflicting* `default` methods, Java refuses to guess —
it's a **compile error** until the implementing class overrides the method itself:

```java
interface A { default String who() { return "A"; } }
interface B { default String who() { return "B"; } }
class C implements A, B {
    public String who() { return A.super.who(); } // must resolve explicitly — no silent pick
}
```

## Practice exercise — "port this C++ habit"

Open [`Exercise.java`](Exercise.java). It's a small program with **five methods**, each
written the way a C++ developer's first instinct would write it in Java — and each
one either won't compile, compiles but misbehaves, or just isn't idiomatic. Your job,
without looking at the answer key in the comments until you're done:

1. Read each method's doc comment (the intended behavior).
2. Fix the C++-ism so the method actually does what it says, using only concepts from
   this module and module 01–02.
3. Run it and confirm the `assert` in `main` passes for each.

```bash
java -ea 00-cpp-vs-java/Exercise.java
```

If you get stuck, the mental-model table above has every fix.

## Run the demo

```bash
java 00-cpp-vs-java/HelloComparison.java
```

## Retrieval practice, hints, and solution

1. Why is Java pass-by-value even for objects?
2. When is deterministic cleanup still required with GC?
3. Which C++ value assumptions fail for Java references?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
