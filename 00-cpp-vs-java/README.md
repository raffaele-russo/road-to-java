# 00 — C++ vs Java: the mental-model shift

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

## Run the demo

```bash
java 00-cpp-vs-java/HelloComparison.java
```
