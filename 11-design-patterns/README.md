# 11 — Design Patterns

GoF patterns are still a staple of Java interviews — partly because Java's constraints
(no multiple class inheritance, no default/named args, no operator overloading, no
free functions) are exactly what push idiomatic code toward these patterns in the
first place. Know *why* each pattern exists in Java specifically, not just the UML.

## Categories

| Category | Concerned with | Patterns here |
|----------|----------------|----------------|
| Creational | how objects get created | Singleton, Factory Method, Builder |
| Structural | how objects are composed | Adapter, Decorator, Facade, Proxy, Composite |
| Behavioral | how objects communicate | Strategy, Observer, Command, Template Method, Iterator, Chain of Responsibility |

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

## Facade — hide a set of subsystems behind one simplified entry point

The subsystems stay fully usable on their own; the facade just spares most callers
from having to coordinate all of them. **Java-specific angle:** Spring's `JdbcTemplate`
is a facade over raw JDBC (`Connection`/`Statement`/`ResultSet` juggling and checked
exceptions) — you rarely see the subsystem it wraps.

## Proxy — stand in for another object to control access to it

Lazy-load an expensive resource, gate access, add caching/logging — all without the
client knowing it isn't talking to the real object. **Java-specific angle:** the hand-rolled
version here is a *static* proxy, but Java also ships a *dynamic* proxy mechanism
(`java.lang.reflect.Proxy`) that generates the proxy class from an interface at runtime —
that's what powers Spring AOP and Hibernate's lazy-loaded entity proxies.

## Composite — treat individual objects and groups uniformly

Client code recurses over a tree without caring whether a node is a leaf or a branch.
**Java-specific angle:** a `sealed interface ... permits File, Directory` (Java 17+) documents
the closed set of node types in the type system itself, pairing naturally with exhaustive
`switch` pattern matching (module 08) as an alternative to virtual dispatch.

## Strategy — swap an algorithm at runtime

**Java-specific angle:** since Java 8, you rarely need a `Strategy` *interface* with
multiple classes — a `java.util.function.Function`/`Comparator`/lambda often *is* the
strategy. Know both the classic OOP form and the lambda-based modern form; interviewers
ask "how did Java 8 change this pattern?"

## Observer — one-to-many notification

Classic pub/sub. Java has ad-hoc support (deprecated `java.util.Observable`, no longer
recommended) — in practice you write a tiny custom implementation (shown here) or reach
for a framework (Spring events, RxJava). Know the pattern; don't rely on `Observable`.

## Command — turn a request into an object

Encapsulating a request lets it be queued, logged, or undone instead of being an
immediate direct call. **Java-specific angle:** a fire-and-forget command with no undo
can often just be a `Runnable`/lambda — no class needed. Undo still needs a class here
because it has to capture the state to reverse. Good contrast with Strategy, which swaps
a whole algorithm rather than encapsulating a single request.

## Template Method — fix an algorithm's skeleton, let subclasses override its steps

**Java-specific angle:** one of the few remaining places Java's single inheritance model
is load-bearing — the fixed skeleton lives in one abstract class (often `final`), so a
subclass can only override its steps, never reorder them. Contrast with Strategy:
Strategy swaps a whole algorithm via composition; Template Method fixes the algorithm's
shape and overrides pieces via inheritance.

## Iterator — traverse a collection without exposing its representation

**Java-specific angle:** the enhanced for-loop is sugar over exactly this — `for (T x : xs)`
compiles down to calling `iterator()` then looping on `hasNext()`/`next()`. Implement
`Iterable` yourself and you get that syntax for free. Streams/Spliterator (module 05) are
the modern successor for bulk/aggregate operations over a sequence.

## Chain of Responsibility — pass a request along a chain until something handles it

The sender doesn't need to know which handler (if any) will act. **Real-world Java
examples you should cite:** Servlet `FilterChain`, Spring Security's filter chain — each
filter decides to act and/or forward to the next one in line.

## Run

```bash
java 11-design-patterns/Singleton.java
java 11-design-patterns/FactoryMethod.java
java 11-design-patterns/Builder.java
java 11-design-patterns/Adapter.java
java 11-design-patterns/Decorator.java
java 11-design-patterns/Facade.java
java 11-design-patterns/Proxy.java
java 11-design-patterns/Composite.java
java 11-design-patterns/Strategy.java
java 11-design-patterns/Observer.java
java 11-design-patterns/Command.java
java 11-design-patterns/TemplateMethod.java
java 11-design-patterns/Iterator.java
java 11-design-patterns/ChainOfResponsibility.java
```
