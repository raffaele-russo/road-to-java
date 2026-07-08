# 09 — Interview Q&A (rapid fire)

Answers kept short and quotable. If you can say these fluently, you're in good shape.

## Language & semantics

**Q: Is Java pass-by-value or pass-by-reference?**
Always pass-by-value. For objects, the value copied is the reference — so you can mutate
the object but not reassign the caller's variable.

**Q: `==` vs `.equals()`?**
`==` compares references (identity) for objects, values for primitives. `.equals()`
compares logical value; override it (with `hashCode`) for value types.

**Q: `equals`/`hashCode` contract?**
Equal objects must have equal hash codes. Override both together or hash collections break.

**Q: `final` vs `finally` vs `finalize`?**
`final` = non-reassignable variable / non-overridable method / non-extendable class.
`finally` = block that always runs after try/catch. `finalize` = deprecated GC hook, don't use.

**Q: `String`, `StringBuilder`, `StringBuffer`?**
`String` immutable. `StringBuilder` mutable, not thread-safe (fast). `StringBuffer`
mutable, synchronized (legacy, slower).

**Q: Why is `String` immutable?**
Security, thread-safety, safe use as `HashMap` keys, and the string pool / interning.

**Q: Autoboxing pitfalls?**
`Integer` caching (-128..127) breaks `==`; unboxing `null` throws NPE; boxing costs in loops.

## OOP

**Q: Abstract class vs interface?**
Abstract class: state + partial impl, single inheritance. Interface: contract/capability,
multiple inheritance, `default`/`static` methods, only constants for fields.

**Q: Overloading vs overriding?**
Overloading = same name, different params, resolved at compile time. Overriding = subclass
replaces a virtual method, resolved at runtime (dynamic dispatch).

**Q: Can you override a static method?**
No — static methods are hidden, not overridden. No polymorphism.

**Q: Are Java methods virtual?**
Yes, by default. Use `final`/`static`/`private` to prevent overriding.

## Collections & generics

**Q: `ArrayList` vs `LinkedList`?**
`ArrayList`: array-backed, O(1) random access, cache-friendly — default. `LinkedList`:
O(1) ends, O(n) access, rarely worth it.

**Q: `HashMap` internals?**
Array of buckets; index = `hash(key) & (n-1)`. Collisions chain in a linked list, which
becomes a red-black tree past a threshold (Java 8+). Resizes/rehashes at load factor 0.75.

**Q: `HashMap` vs `Hashtable` vs `ConcurrentHashMap`?**
`HashMap` unsynchronized, allows null. `Hashtable` fully synchronized, legacy.
`ConcurrentHashMap` segmented/lock-striped concurrency, no null keys/values.

**Q: `HashMap` vs `TreeMap` vs `LinkedHashMap`?**
Hash: O(1), unordered. Tree: O(log n), sorted keys. Linked: O(1), insertion/access order.

**Q: What is type erasure?**
Generics exist only at compile time; runtime sees raw types. Hence no `new T()`,
`new T[]`, `List<int>`, or `instanceof List<String>`.

**Q: PECS?**
Producer Extends, Consumer Super. `? extends T` to read, `? super T` to write.

**Q: fail-fast vs fail-safe iterators?**
Fail-fast (`ArrayList`, `HashMap`) throw `ConcurrentModificationException` on structural
change during iteration. Fail-safe (`CopyOnWriteArrayList`, `ConcurrentHashMap`) iterate a
snapshot.

## Exceptions

**Q: Checked vs unchecked?**
Checked (subclass `Exception`) must be caught/declared — recoverable conditions.
Unchecked (`RuntimeException`/`Error`) not enforced — bugs / fatal.

**Q: try-with-resources?**
Auto-closes `AutoCloseable` resources at block end, reverse order, even on exception —
Java's deterministic-cleanup / RAII substitute.

## Concurrency

**Q: `synchronized` vs `volatile`?**
`synchronized`: mutual exclusion + visibility. `volatile`: visibility + no reordering,
but no atomicity — bad for compound ops like `count++`.

**Q: What is the Java Memory Model / happens-before?**
Rules guaranteeing when one thread's writes are visible to another. Unlock → lock,
volatile write → read, `start()` → run, run → `join()`.

**Q: `start()` vs `run()`?**
`start()` spawns a new thread; `run()` executes on the current thread.

**Q: How to make a class thread-safe?**
Immutability, `synchronized`/locks, atomics, concurrent collections, or confinement.

**Q: What are virtual threads (Java 21)?**
JVM-scheduled lightweight threads; millions possible. Blocking code scales like async —
ideal for I/O-bound workloads.

## JVM & memory

**Q: Stack vs heap?**
Stack: per-thread frames, locals, primitives, references. Heap: all objects, GC-managed.

**Q: How does GC decide what to collect?**
Objects unreachable from GC roots (stack, statics, JNI) are eligible. Generational:
young (minor GC) vs old (major GC).

**Q: Can Java have memory leaks?**
Yes — unintended reachability (static caches, unremoved listeners, `ThreadLocal`s).

**Q: What is the JIT?**
Runtime compiler turning hot bytecode into optimized native code; tiered C1/C2.

**Q: JDK vs JRE vs JVM?**
JVM runs bytecode. JRE = JVM + core libs. JDK = JRE + compiler/tools (`javac`, `jar`).

## Design patterns

**Q: Why is enum the recommended Singleton implementation?**
Free serialization-safety and reflection-safety (a normal class's private constructor
can still be invoked via reflection; enums can't be instantiated that way). Also
thread-safe by construction — no double-checked locking needed.

**Q: Why does double-checked-locking Singleton need `volatile`?**
Without it, the JMM permits instruction reordering that lets another thread observe a
reference to a *partially constructed* object — the field write can appear to happen
before the constructor finishes.

**Q: Builder vs telescoping constructors?**
Java has no named/default parameters, so multiple overloaded constructors
(`Foo(a)`, `Foo(a,b)`, `Foo(a,b,c)`) get unreadable fast. A fluent builder scales to many
optional fields and can validate at `build()` time.

**Q: Decorator vs inheritance?**
Decorator composes behavior at runtime by wrapping the same interface (like `java.io`
stream classes); avoids a combinatorial explosion of subclasses for every combination
of behaviors.

**Q: How did Java 8 change the Strategy pattern?**
A single-method strategy interface can now be satisfied by a lambda instead of a named
class — `Comparator`, `Function`, `Predicate` are all "strategy" slots you fill inline.

**Q: Adapter vs Decorator vs Facade — what's the difference?**
Adapter: makes an *incompatible* interface *compatible* (translates). Decorator: adds
behavior to a *compatible* interface (same shape, more responsibility). Facade:
simplifies a *complex subsystem* behind one easy interface (no wrapping of a single object).

## Testing

**Q: How do you unit test a class with dependencies?**
Inject dependencies (constructor injection) so they can be replaced with test doubles;
mock them with Mockito, stub the calls you need, and assert on the class under test's
behavior in isolation.

**Q: Mock vs Spy?**
Mock: a bare fake, everything returns null/0/false unless stubbed. Spy: wraps a *real*
object, real methods run unless you explicitly override them.

**Q: Why prefer `assertThrows` over try/catch + fail()?**
Shorter, and it fails clearly if *no* exception is thrown (a bare try/catch that doesn't
call `fail()` in the try block can silently pass a test that never threw).

**Q: What's a flaky test and what usually causes it?**
A test that passes/fails nondeterministically — usually real time (`Thread.sleep`,
`System.currentTimeMillis`), unmocked randomness, real network calls, or shared mutable
state leaking between tests (missing `@BeforeEach` reset).

## Spring / DI

**Q: What problem does Dependency Injection solve?**
Decouples an object from constructing its own dependencies — a container wires them in,
so implementations can be swapped (real vs mock) and configuration is centralized.

**Q: Constructor injection vs field `@Autowired`?**
Constructor injection makes dependencies explicit and `final`, fails fast at startup if a
bean is missing, and works in plain unit tests without a Spring container at all.

**Q: `@Component` vs `@Bean`?**
`@Component` annotates a class you own, picked up by `@ComponentScan`. `@Bean` is a
factory method inside a `@Configuration` class — for wiring types you don't own/can't annotate.

**Q: What does `@SpringBootApplication` expand to?**
`@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.

**Q: Spring singleton scope vs the GoF Singleton pattern (module 11)?**
A Spring singleton bean is one instance **per container** (you can have multiple
containers, or refresh one), not one instance per JVM like the classic GoF pattern.

## Design / good taste

**Q: SOLID in one line each?**
Single responsibility, Open/closed, Liskov substitution, Interface segregation,
Dependency inversion.

**Q: Favorite Java idioms you'd bring from C++ experience?**
Program to interfaces, immutability by default (records/`final`), `Optional` over null,
prefer composition over inheritance, streams for declarative transforms.
