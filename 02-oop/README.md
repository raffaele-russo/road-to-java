# 02 — OOP: classes, interfaces, inheritance

## Learning outcome and prerequisite

**Outcome:** Design classes with explicit identity, equality, initialization, mutability, and inheritance contracts.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

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

## Constructor & initialization order (interview favorite)

When you write `new Child()`, Java does **not** start by running the visible lines
inside `Child()`. A constructor must first build the parent part of the object.

Precise sequence for a `Child()` constructor that uses the implicit `super()`:

1. Load/initialize `Parent` statics, if not already initialized.
2. Load/initialize `Child` statics, if not already initialized.
3. Allocate the object; all instance fields start with default values (`0`, `false`, `null`).
4. Enter `Child()`. Its first action is the implicit `super()` call.
5. Enter `Parent()`. Its first action is `super()` to `Object`.
6. After `Object` returns, run `Parent` instance field initializers and instance
   initializer blocks, in source order.
7. Run the rest of the `Parent` constructor body.
8. Return to `Child()`.
9. Run `Child` instance field initializers and instance initializer blocks, in
   source order.
10. Run the rest of the `Child` constructor body.

Think of construction as flowing **down the inheritance chain to `Object` first**,
then executing initialization **back up from parent to child**.

```java
class Parent {
    static String parentStatic = log("1. Parent static field");
    static { log("2. Parent static block"); }

    String parentField = log("5. Parent instance field");
    { log("6. Parent instance block"); }

    Parent() {
        // super() to Object is inserted here by the compiler.
        log("7. Parent constructor body");
    }

    static String log(String message) {
        System.out.println(message);
        return message;
    }
}

class Child extends Parent {
    static String childStatic = log("3. Child static field");
    static { log("4. Child static block"); }

    String childField = log("8. Child instance field");
    { log("9. Child instance block"); }

    Child() {
        // super() is inserted here by the compiler, before this body runs.
        log("10. Child constructor body");
    }

    public static void main(String[] args) {
        new Child();
    }
}
```

Output:

```text
1. Parent static field
2. Parent static block
3. Child static field
4. Child static block
5. Parent instance field
6. Parent instance block
7. Parent constructor body
8. Child instance field
9. Child instance block
10. Child constructor body
```

Run `new Child()` a second time in the same program and lines 1-4 will **not**
print again: static initialization belongs to the class, not to each object.

**Gotcha to name out loud:** calling an overridable method from a parent constructor is
dangerous — the subclass override runs *before* the subclass's own fields are initialized.

```java
class Base {
    Base() { init(); }          // calls the OVERRIDDEN version in Derived
    void init() { }
}
class Derived extends Base {
    private final String name = "set";
    @Override void init() { System.out.println(name); }  // prints null — not "set" yet!
}
```

## Nested classes — four kinds

| Kind | Holds outer ref? | Use when |
|------|-------------------|----------|
| **Static nested** | no | groups a helper class with its owner; the default choice |
| **Inner (non-static)** | yes, implicitly (`Outer.this`) | needs the enclosing instance's state |
| **Local** (declared inside a method) | yes | used only within that one method |
| **Anonymous** (`new Interface() { ... }`) | yes | one-off implementation, no reusable name — mostly superseded by lambdas for functional interfaces |

```java
class Outer {
    int x = 10;
    class Inner { int getX() { return x; } }              // implicit Outer.this.x
    static class Nested { int compute() { return 42; } }   // no outer instance needed

    Runnable makeTask() {
        return new Runnable() {                            // anonymous class
            public void run() { System.out.println(x); }   // still captures Outer.this
        };
    }
}
Outer o = new Outer();
Outer.Inner inner = o.new Inner();     // needs an enclosing instance to construct
Outer.Nested nested = new Outer.Nested(); // does not
```

## Interface evolution: `static` and `private` methods (Java 8/9)

- `default` methods (Java 8): provide a body, overridable by implementers.
- `static` methods (Java 8): utility methods namespaced to the interface, not inherited.
- `private` methods (Java 9): share code *between* default methods without exposing it.

```java
interface Validator {
    boolean isValid(String s);
    static Validator notBlank() { return s -> !s.isBlank(); }   // static factory on the interface
    default Validator and(Validator other) {
        return s -> this.isValid(s) && other.isValid(s);
    }
    private void log(String s) { System.out.println("checked: " + s); } // helper, not part of the API
}
```

## Covariant return types

An override may narrow the return type to a subtype — useful for fluent/builder APIs and
factory-method-style overrides.

```java
class Animal { Animal reproduce() { return new Animal(); } }
class Dog extends Animal {
    @Override Dog reproduce() { return new Dog(); }   // narrower return type — legal override
}
```

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

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). It gives you a `Shape` abstract class and a
`Describable` interface with a `default` method, already written. Your job:

1. Implement `Circle` and `Rectangle`, each extending `Shape` **and** implementing
   `Describable`.
2. Correctly override `equals`/`hashCode`/`toString` on both (value equality — two
   `Circle`s with the same radius are equal; a `Circle` and a `Rectangle` are never
   equal to each other even with matching "areas").
3. Implement `Shape.totalArea(List<Shape> shapes)` as a `static` method.

```bash
java -ea 02-oop/Exercise.java
```

## Run

```bash
java 02-oop/Oop.java
```

## Retrieval practice, hints, and solution

1. When should equality use value rather than identity?
2. What does `final` guarantee for a mutable field value?
3. Why can calling overridable methods in constructors fail?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
