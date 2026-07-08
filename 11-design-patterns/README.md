# 11 — Design Patterns

GoF patterns are still a staple of Java interviews — partly because Java's constraints
(no multiple class inheritance, no default/named args, no operator overloading, no
free functions) are exactly what push idiomatic code toward these patterns in the
first place. Know *why* each pattern exists in Java specifically, not just the UML.

## Categories

| Category | Concerned with | Patterns here |
|----------|----------------|----------------|
| Creational | how objects get created | Singleton, Factory Method, Builder |
| Structural | how objects are composed | Adapter, Decorator |
| Behavioral | how objects communicate | Strategy, Observer |

## Singleton — one instance, global access point

**Java-specific angle:** the enum singleton is the recommended form because it's
serialization-safe and reflection-safe for free (a C++ Meyers' singleton has no
equivalent worry, but no equivalent free defense either).

```java
enum Config { INSTANCE; int value = 42; }
Config.INSTANCE.value;
```
Naive lazy versions need `synchronized` or double-checked locking (with `volatile`) —
a classic follow-up question: "why does double-checked locking need `volatile`?"
(Answer: without it, another thread can observe a partially-constructed object due to
instruction reordering — the JMM allows it.)

## Factory Method — defer instantiation to subclasses/a creator method

Decouples client code from concrete classes. In modern Java, often collapses into a
static method returning a sealed-interface type (module 08) — no need for a class
hierarchy of factories when a `switch` over a sealed type is exhaustive and cheap.

## Builder — construct complex immutable objects step by step

**Java-specific angle:** Java has no named/default parameters and (rightly) discourages
telescoping constructors (`Foo(a)`, `Foo(a,b)`, `Foo(a,b,c)`...). The fluent builder is
the idiomatic replacement, and it composes well with immutability (build once, then the
object is `final`-field immutable). Records (module 08) replace *simple* data holders,
but Builder still wins when construction has optional/defaulted fields or validation.

## Adapter — make an incompatible interface compatible

Wraps an existing class behind the interface a client expects, without altering either.
Extremely common at API boundaries / when integrating a legacy or third-party class.

## Decorator — attach behavior to an object dynamically, without subclassing

**Real-world Java example you should cite:** `java.io` streams —
`new BufferedReader(new InputStreamReader(System.in))` is decorators all the way down.
Each layer wraps the same interface and adds behavior. Prefer this over creating a
combinatorial explosion of subclasses (`FastBufferedReader`, `SlowBufferedReader`, ...).

## Strategy — swap an algorithm at runtime

**Java-specific angle:** since Java 8, you rarely need a `Strategy` *interface* with
multiple classes — a `java.util.function.Function`/`Comparator`/lambda often *is* the
strategy. Know both the classic OOP form and the lambda-based modern form; interviewers
ask "how did Java 8 change this pattern?"

## Observer — one-to-many notification

Classic pub/sub. Java has ad-hoc support (deprecated `java.util.Observable`, no longer
recommended) — in practice you write a tiny custom implementation (shown here) or reach
for a framework (Spring events, RxJava). Know the pattern; don't rely on `Observable`.

## Run

```bash
java 11-design-patterns/Singleton.java
java 11-design-patterns/FactoryMethod.java
java 11-design-patterns/Builder.java
java 11-design-patterns/Adapter.java
java 11-design-patterns/Decorator.java
java 11-design-patterns/Strategy.java
java 11-design-patterns/Observer.java
```
