# 02 — OOP: classes, interfaces, inheritance

## Classes

- One `public` top-level class per file; file name must match.
- No header/impl split — declaration and definition together.
- Fields default-initialized (0/false/null); locals must be explicitly initialized.
- `this` is a reference, accessed with `.` (no `->`).
- Constructors can be overloaded and chained with `this(...)`.

```java
public class Point {
    private final int x, y;                 // final field = set once in ctor
    public Point(int x, int y) { this.x = x; this.y = y; }
    public int x() { return x; }
    public int y() { return y; }
}
```

## Access modifiers

| Modifier | Visible to |
|----------|-----------|
| `public` | everyone |
| `protected` | package + subclasses |
| *(none)* / package-private | same package only |
| `private` | same class only |

No `friend`. Package-private is the default (unlike C++ `private` default for classes).

## Inheritance

- Single inheritance of classes with `extends`.
- All methods are **virtual by default** (opposite of C++). Use `final` to prevent overriding.
- `super(...)` calls a parent constructor; `super.method()` calls parent version.
- `@Override` annotation is optional but strongly recommended — the compiler verifies it.
- Every class implicitly extends `java.lang.Object`.

```java
class Animal { String sound() { return "..."; } }
class Dog extends Animal {
    @Override String sound() { return "woof"; }  // virtual dispatch
}
```

## Abstract classes vs interfaces

| | Abstract class | Interface |
|-|----------------|-----------|
| Instantiate | no | no |
| Fields | yes (state) | only `static final` constants |
| Constructors | yes | no |
| Multiple inheritance | no | **yes** — a class implements many |
| Method bodies | yes | `default` and `static` methods (Java 8+) |
| Use when | shared state + partial impl | capability / contract |

**Interface with default methods** is Java's answer to multiple inheritance and is
how the language evolves APIs without breaking implementers.

```java
interface Greeter {
    String name();
    default String greet() { return "Hello, " + name(); } // has a body
}
```

## `Object` methods you must know

Every object inherits these — override them thoughtfully:

- `equals(Object)` — value equality. Default is reference equality (`==`).
- `hashCode()` — **must** be consistent with `equals`: equal objects → equal hash codes. Required for `HashMap`/`HashSet` keys.
- `toString()` — human-readable form.

**The contract:** if you override `equals`, you *must* override `hashCode`, or hash-based
collections will silently misbehave. Records (module 08) generate all three for you.

```java
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Point p)) return false;   // pattern matching instanceof
    return x == p.x && y == p.y;
}
@Override public int hashCode() { return Objects.hash(x, y); }
```

## `static`

- `static` members belong to the class, not instances — the closest thing to C++ free functions/globals.
- `static` nested classes don't hold a reference to an outer instance (prefer these); inner (non-static) classes do.

## Enums are real classes

```java
enum Planet {
    EARTH(9.8), MARS(3.7);
    private final double gravity;
    Planet(double g) { this.gravity = g; }
    double gravity() { return gravity; }
}
```
Far more powerful than C++ enums — they have fields, methods, and can implement interfaces.

## Run

```bash
java 02-oop/Oop.java
```
