# 09 — Interview Q&A (rapid fire)

## Learning outcome and prerequisite

**Outcome:** Retrieve Java mental models and communicate contracts, evidence, and tradeoffs precisely.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

Answers kept short and quotable, with a small example under each one to make the
concept concrete. If you can say the bold line fluently *and* reconstruct the
example, you're in good shape.

## Language & semantics

**Q: Is Java pass-by-value or pass-by-reference?**
Always pass-by-value. For objects, the value copied is the reference — so you can mutate
the object but not reassign the caller's variable.

```java
void reassign(StringBuilder sb) { sb = new StringBuilder("new"); }  // caller unaffected
void mutate(StringBuilder sb)   { sb.append("!"); }                 // caller sees the change

StringBuilder s = new StringBuilder("hi");
reassign(s);   // s is still "hi"       — the local parameter `sb` was repointed, not `s`
mutate(s);     // s is now "hi!"        — same object, mutated through the copied reference
```

**Q: `==` vs `.equals()`?**
`==` compares references (identity) for objects, values for primitives. `.equals()`
compares logical value; override it (with `hashCode`) for value types.

```java
String a = new String("hi");
String b = new String("hi");
a == b;          // false — two distinct objects
a.equals(b);     // true  — same characters

Integer x = 1000, y = 1000;
x == y;          // false — outside the Integer cache, so two distinct boxes
x.equals(y);     // true
```

**Q: `equals`/`hashCode` contract?**
Equal objects must have equal hash codes. Override both together or hash collections break.

```java
record Point(int x, int y) {}   // records generate a correct equals()/hashCode() pair for you

Set<Point> seen = new HashSet<>();
seen.add(new Point(1, 2));
seen.contains(new Point(1, 2));   // true — same hashCode() bucket, equals() matches

// Breaking the contract manually (equals() overridden, hashCode() left as Object's
// identity hash) means this same lookup would return false — the object "is equal"
// but lands in the wrong bucket.
```

**Q: `final` vs `finally` vs `finalize`?**
`final` = non-reassignable variable / non-overridable method / non-extendable class.
`finally` = block that always runs after try/catch. `finalize` = deprecated GC hook, don't use.

```java
final int MAX = 10;              // MAX = 20; would not compile

try {
    riskyCall();
} catch (IOException e) {
    log(e);
} finally {
    connection.close();          // runs whether riskyCall() threw or not
}
```

**Q: `String`, `StringBuilder`, `StringBuffer`?**
`String` immutable. `StringBuilder` mutable, not thread-safe (fast). `StringBuffer`
mutable, synchronized (legacy, slower).

```java
String s = "";
for (int i = 0; i < 10_000; i++) s += i;        // O(n²) — allocates a new String every time

StringBuilder sb = new StringBuilder();
for (int i = 0; i < 10_000; i++) sb.append(i);  // O(n) — mutates one internal buffer
String result = sb.toString();
```

**Q: Why is `String` immutable?**
Security, thread-safety, safe use as `HashMap` keys, and the string pool / interning.

```java
String a = "hello";
String b = "hello";
a == b;                       // true — both point at the same interned literal

void connect(String host) { ... }   // safe: nobody can mutate `host` out from under you
```

**Q: Autoboxing pitfalls?**
`Integer` caching (-128..127) breaks `==`; unboxing `null` throws NPE; boxing costs in loops.

```java
Integer a = 127, b = 127;
a == b;                 // true  — cached, same object

Integer c = 128, d = 128;
c == d;                 // false — outside the cache, two distinct objects

Map<String, Integer> m = new HashMap<>();
int n = m.get("missing");   // NullPointerException — get() returns null, auto-unboxing dies

long total = 0;
for (Long boxed : bigListOfLongs) total += boxed;  // one unbox per iteration — measurable cost
```

## OOP

**Q: Abstract class vs interface?**
Abstract class: state + partial impl, single inheritance. Interface: contract/capability,
multiple inheritance, `default`/`static` methods, only constants for fields.

```java
abstract class Animal {                    // can hold state
    protected final String name;
    Animal(String name) { this.name = name; }
    abstract String sound();                // must be implemented by subclasses
    String describe() { return name + " says " + sound(); }  // shared, partial impl
}

interface Flyer {                           // pure capability, no state
    void fly();
    default void takeOff() { System.out.println("wings up"); }  // default method
}

class Bird extends Animal implements Flyer {   // one class extends, many interfaces implements
    Bird(String name) { super(name); }
    String sound() { return "tweet"; }
    public void fly() { System.out.println(name + " flying"); }
}
```

**Q: Overloading vs overriding?**
Overloading = same name, different params, resolved at compile time. Overriding = subclass
replaces a virtual method, resolved at runtime (dynamic dispatch).

```java
class Base {
    void greet(String s) { System.out.println("Base: " + s); }
    void speak() { System.out.println("Base speaks"); }
}
class Derived extends Base {
    void greet(int i) { System.out.println("Derived: " + i); }   // overload: new signature
    @Override void speak() { System.out.println("Derived speaks"); }  // override: same signature
}
```

**Q: Can you override a static method?**
No — static methods are hidden, not overridden. No polymorphism.

```java
class A { static String who() { return "A"; } }
class B extends A { static String who() { return "B"; } }

A ref = new B();
ref.who();     // "A" — resolved by the *compile-time* (reference) type, not the runtime type
B.who();       // "B" — call it through the right class instead
```

**Q: Are Java methods virtual?**
Yes, by default. Use `final`/`static`/`private` to prevent overriding.

```java
class Base { void speak() { System.out.println("Base"); } }
class Derived extends Base { @Override void speak() { System.out.println("Derived"); } }

Base b = new Derived();
b.speak();     // "Derived" — dynamic dispatch picks the *runtime* type's override
```

## Collections & generics

**Q: `ArrayList` vs `LinkedList`?**
`ArrayList`: array-backed, O(1) random access, cache-friendly — default. `LinkedList`:
O(1) ends, O(n) access, rarely worth it.

```java
List<Integer> arr = new ArrayList<>(Collections.nCopies(1_000_000, 0));
arr.get(500_000);          // O(1) — direct array index

List<Integer> linked = new LinkedList<>(arr);
linked.get(500_000);       // O(n) — walks half the list to get there
```

**Q: `HashMap` internals?**
Array of buckets; index = `hash(key) & (n-1)`. Collisions chain in a linked list, which
becomes a red-black tree past a threshold (Java 8+). Resizes/rehashes at load factor 0.75.

```java
// simplified version of what HashMap.hash()/put() does internally
int h = key.hashCode();
h ^= (h >>> 16);                 // spread the high bits into the low bits
int bucketIndex = h & (table.length - 1);   // table.length is always a power of 2

// table.length = 16, size hits 12 (0.75 load factor) -> resize to 32 and rehash everything
```

**Q: `HashMap` vs `Hashtable` vs `ConcurrentHashMap`?**
`HashMap` unsynchronized, allows null. `Hashtable` fully synchronized, legacy.
`ConcurrentHashMap` segmented/lock-striped concurrency, no null keys/values.

```java
new HashMap<String, Integer>().put(null, 1);            // fine — one null key allowed
new Hashtable<String, Integer>().put(null, 1);           // NullPointerException
new ConcurrentHashMap<String, Integer>().put(null, 1);   // NullPointerException — ambiguous
                                                          // ("absent" vs "present with null value")
                                                          // under concurrent access
```

**Q: `HashMap` vs `TreeMap` vs `LinkedHashMap`?**
Hash: O(1), unordered. Tree: O(log n), sorted keys. Linked: O(1), insertion/access order.

```java
// LinkedHashMap with access-order = a working LRU cache in a handful of lines
Map<String, Integer> lru = new LinkedHashMap<>(16, 0.75f, true) {
    @Override protected boolean removeEldestEntry(Map.Entry<String, Integer> eldest) {
        return size() > 100;   // evict the least-recently-used entry once we're over capacity
    }
};
```

**Q: What is type erasure?**
Generics exist only at compile time; runtime sees raw types. Hence no `new T()`,
`new T[]`, `List<int>`, or `instanceof List<String>`.

```java
List<String> ls = new ArrayList<>();
List<Integer> li = new ArrayList<>();
ls.getClass() == li.getClass();     // true — both are just `ArrayList` at runtime

// won't compile — type argument doesn't exist at runtime to check against:
// if (ls instanceof List<String>) { }
// ls instanceof List<?>            // OK — unbounded wildcard is legal
```

**Q: PECS?**
Producer Extends, Consumer Super. `? extends T` to read, `? super T` to write.

```java
static double sum(List<? extends Number> producer) {   // I only read from it
    double total = 0;
    for (Number n : producer) total += n.doubleValue();
    return total;
}

static void addOneTwoThree(List<? super Integer> consumer) {  // I only write to it
    consumer.add(1);
    consumer.add(2);
    consumer.add(3);
}

sum(List.of(1, 2, 3));                 // List<Integer> is a Number producer — fine
addOneTwoThree(new ArrayList<Number>());   // List<Number> can consume Integers — fine
```

**Q: fail-fast vs fail-safe iterators?**
Fail-fast (`ArrayList`, `HashMap`) throw `ConcurrentModificationException` on structural
change during iteration. Fail-safe (`CopyOnWriteArrayList`, `ConcurrentHashMap`) iterate a
snapshot.

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
for (Integer i : list) {
    if (i == 2) list.remove(i);   // ConcurrentModificationException on the next hasNext()
}

List<Integer> safe = new CopyOnWriteArrayList<>(List.of(1, 2, 3));
for (Integer i : safe) {
    if (i == 2) safe.remove(i);   // fine — iterating a point-in-time snapshot of the array
}
```

**Q: What's a raw type, and why avoid it?**
A generic type used without its type argument (`List` instead of `List<String>`). It
compiles, for backward compatibility, but loses all compile-time type checking.

```java
List raw = new ArrayList();
raw.add("a");
raw.add(42);          // compiles! no type safety — this is exactly what generics prevent
```

**Q: What do `NavigableMap`/`NavigableSet` add over `TreeMap`/`TreeSet`?**
Relative-position lookups: `floorKey`/`ceilingKey`/`higherKey`/`lowerKey`, plus
`headMap`/`tailMap` range views — the actual reason to pay `TreeMap`'s O(log n) instead
of `HashMap`'s O(1).

```java
TreeMap<Integer, String> m = new TreeMap<>(Map.of(10, "a", 20, "b", 30, "c"));
m.floorKey(25);     // 20 — greatest key <= 25
m.ceilingKey(25);   // 30 — smallest key >= 25
```

**Q: What are sequenced collections (Java 21)?**
`SequencedCollection`/`SequencedSet`/`SequencedMap` give a uniform `getFirst()`/
`getLast()`/`addFirst()`/`addLast()`/`reversed()` API across `List`, `LinkedHashSet`,
`LinkedHashMap`, and `ArrayDeque` — before this, each had its own ad-hoc way (or none).

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
list.getFirst();     // 1 — instead of list.get(0)
list.reversed();     // [3, 2, 1] — a VIEW, not a copy
```

## Exceptions

**Q: Checked vs unchecked?**
Checked (subclass `Exception`) must be caught/declared — recoverable conditions.
Unchecked (`RuntimeException`/`Error`) not enforced — bugs / fatal.

```java
void readConfig() throws IOException {        // checked — every caller must handle or propagate
    Files.readString(Path.of("config.json"));
}

int divide(int a, int b) {
    return a / b;   // unchecked ArithmeticException on b == 0 — not declared, not forced on caller
}
```

**Q: try-with-resources?**
Auto-closes `AutoCloseable` resources at block end, reverse order, even on exception —
Java's deterministic-cleanup / RAII substitute.

```java
try (var in = new FileInputStream("a.txt");
     var out = new FileOutputStream("b.txt")) {
    in.transferTo(out);
}   // out.close() runs, then in.close() — reverse declaration order, guaranteed even on exception
```

**Q: What's wrong with `return` inside a `finally` block?**
It silently discards whatever the `try`/`catch` was about to return or throw — a classic
"what does this print" trap.

```java
static int f() {
    try { return 1; } finally { return 2; }  // f() returns 2 — the 1 is silently lost
}
```

**Q: What are suppressed exceptions?**
If both a try-with-resources body *and* `close()` throw, the `close()` exception isn't
lost — it's attached to the primary one via `addSuppressed`, retrievable with
`getSuppressed()` (pre-Java 7, it would silently mask the real exception instead).

```java
try (AutoCloseable r = () -> { throw new IllegalStateException("close failed"); }) {
    throw new RuntimeException("body failed");
} catch (Exception e) {
    e.getMessage();       // "body failed"
    e.getSuppressed();    // [IllegalStateException: close failed]
}
```

## Functional programming & streams

**Q: What is a functional interface?**
An interface with exactly one abstract method — `@FunctionalInterface` documents the
intent. Lambdas and method references are instances of one.

```java
@FunctionalInterface
interface Transformer<T, R> { R apply(T t); }

Transformer<String, Integer> len = String::length;   // a method reference IS a Transformer
Transformer<String, Integer> lenLambda = s -> s.length();  // equivalent, spelled as a lambda
```

**Q: How do lambdas capture variables?**
By value, and only "effectively final" locals — no captures-by-reference. You can still
mutate a captured *object's* state, just not reassign the local itself.

```java
int base = 10;                                  // effectively final — never reassigned
Function<Integer, Integer> addBase = x -> x + base;   // captured by value, fine

List<Runnable> tasks = new ArrayList<>();
for (int i = 0; i < 3; i++) {
    int copy = i;                                // must snapshot — `i` itself isn't effectively final
    tasks.add(() -> System.out.println(copy));
}
tasks.forEach(Runnable::run);   // prints 0, 1, 2
```

**Q: What are the 4 kinds of method references?**
Static (`Integer::parseInt`), bound instance (`obj::method`), unbound/arbitrary instance
(`String::toUpperCase`), constructor (`ArrayList::new`).

```java
Function<String, Integer> a = Integer::parseInt;      // static method
Supplier<String> b = "hi"::toUpperCase;                 // bound instance — receiver fixed
Function<String, String> c = String::toUpperCase;       // unbound — receiver is the argument
Supplier<List<String>> d = ArrayList::new;               // constructor reference
```

**Q: Are streams lazy?**
Yes — intermediate ops (`filter`, `map`, `sorted`) just build a pipeline description;
nothing runs until a terminal op (`collect`, `forEach`, `reduce`) is called.

```java
Stream<Integer> pipeline = Stream.of(1, 2, 3)
    .peek(x -> System.out.println("saw " + x))
    .filter(x -> x > 1);
// nothing printed yet — no terminal operation has run

pipeline.count();   // *now* "saw 1", "saw 2", "saw 3" print, as the pipeline finally executes
```

**Q: Can you reuse a `Stream`?**
No — single-use/single-traversal. Calling a terminal op a second time throws
`IllegalStateException`.

```java
Stream<Integer> s = Stream.of(1, 2, 3);
s.count();     // 3
s.count();     // IllegalStateException: stream has already been operated upon or closed
```

**Q: `map` vs `flatMap`?**
`map` transforms each element 1:1. `flatMap` maps each element to a stream and flattens
the results — for one-to-many transforms (e.g. list of lists → single stream).

```java
List<List<Integer>> nested = List.of(List.of(1, 2), List.of(3, 4));

nested.stream().map(List::size).toList();          // [2, 2]         — 1:1
nested.stream().flatMap(List::stream).toList();     // [1, 2, 3, 4]   — flattened
```

**Q: When do parallel streams actually help?**
Only for large, CPU-bound, stateless, side-effect-free work over a splittable source.
Overhead and contention on the shared common `ForkJoinPool` can make small/IO-bound
pipelines slower — measure first.

```java
// good fit: large, CPU-bound, stateless
IntStream.rangeClosed(2, 1_000_000).parallel().filter(Interview::isPrime).count();

// bad fit: tiny source, per-element overhead dwarfs the actual work
List.of(1, 2, 3).parallelStream().map(x -> x * 2).toList();   // just use a sequential stream
```

**Q: `Optional` best practices?**
Use it as a return type for "might not find a value" — not as a field or a method
parameter. Never call `.get()` unchecked; prefer `map`/`orElse`/`orElseThrow`.

```java
Optional<User> findById(String id) { ... }              // good: signals "may be absent"

findById("42").map(User::name).orElse("unknown");        // good — no unchecked get()
findById("42").orElseThrow(() -> new NotFoundException("42"));

// avoid:
findById("42").get();          // throws NoSuchElementException with no context if empty
class User { Optional<String> nickname; }   // avoid Optional as a field type — just allow null
```

**Q: `reduce` — what's the identity argument for?**
The accumulator's starting value (e.g. `0` for sum, `1` for product) — also the result
returned if the stream is empty.

```java
List.of(1, 2, 3).stream().reduce(0, Integer::sum);        // 6
List.<Integer>of().stream().reduce(0, Integer::sum);      // 0 — identity returned, no elements
List.of(2, 3, 4).stream().reduce(1, (a, b) -> a * b);      // 24 — identity is 1 for a product
```

## Concurrency

**Q: `synchronized` vs `volatile`?**
`synchronized`: mutual exclusion + visibility. `volatile`: visibility + no reordering,
but no atomicity — bad for compound ops like `count++`.

```java
class Counter {
    private volatile boolean shutdown;       // single flag write/read — volatile is enough
    private int count;

    synchronized void increment() { count++; }   // read-modify-write needs mutual exclusion too
    void requestShutdown() { shutdown = true; }
    boolean isShutdown() { return shutdown; }
}
```

**Q: What is the Java Memory Model / happens-before?**
Rules guaranteeing when one thread's writes are visible to another. Unlock → lock,
volatile write → read, `start()` → run, run → `join()`.

```java
volatile boolean ready = false;
int value = 0;

// Thread A                          // Thread B
value = 42;                          if (ready) {                 // volatile read
ready = true;   // volatile write        System.out.println(value);  // guaranteed to see 42
                                      }
```

**Q: `start()` vs `run()`?**
`start()` spawns a new thread; `run()` executes on the current thread.

```java
Thread t = new Thread(() -> System.out.println(Thread.currentThread().getName()));
t.run();     // prints "main"     — no new thread, just a normal method call
t.start();   // prints "Thread-0" — actually spawns and schedules a new thread
```

**Q: How to make a class thread-safe?**
Immutability, `synchronized`/locks, atomics, concurrent collections, or confinement.

```java
final class ImmutablePoint {                          // 1. immutability
    final int x, y;
    ImmutablePoint(int x, int y) { this.x = x; this.y = y; }
}

class AtomicCounter {                                  // 2. atomics
    private final AtomicInteger count = new AtomicInteger();
    void inc() { count.incrementAndGet(); }
}

Map<String, Integer> shared = new ConcurrentHashMap<>();  // 3. concurrent collections
```

**Q: What are virtual threads (Java 21)?**
JVM-scheduled lightweight threads; millions possible. Blocking code scales like async —
ideal for I/O-bound workloads.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));   // blocking call — cheap on a virtual thread
            return null;
        });
    }
}   // 100k platform threads would exhaust the OS; 100k virtual threads is routine
```

**Q: `Callable` vs `Runnable`?**
`Runnable.run()` returns nothing and can't throw a checked exception. `Callable<V>.call()`
returns a value and can throw a checked exception — submit it to an `ExecutorService` and
get a `Future<V>` back.

```java
Future<Integer> f = pool.submit(() -> 21 + 21);   // Callable<Integer> via lambda
f.get();   // 42, blocks until done
```

**Q: How do you shut down an `ExecutorService` correctly?**
`shutdown()` first (graceful — stop accepting new tasks, finish queued ones), then
`awaitTermination(timeout, unit)`, falling back to `shutdownNow()` (interrupts running
tasks) if it doesn't finish in time. Forgetting to shut down at all leaks non-daemon
threads that keep the JVM alive.

**Q: What is `ThreadLocal` for, and what's the gotcha?**
Gives each thread its own independent copy of a variable — no synchronization needed.
Gotcha: in a pooled-thread environment (executors, servlets), the pool reuses threads,
so a forgotten value outlives its logical task — call `.remove()` when done, or leak
memory/leak data across requests.

**Q: Deadlock vs livelock vs starvation?**
Deadlock: threads block each other forever, each holding what the other needs. Livelock:
threads keep changing state in response to each other but make no real progress. Starvation:
a thread never gets scheduled/never gets the lock because others are always prioritized.

## JVM & memory

**Q: Stack vs heap?**
Stack: per-thread frames, locals, primitives, references. Heap: all objects, GC-managed.

```java
void method() {
    int x = 5;                     // lives on the stack — a primitive local
    Point p = new Point(1, 2);     // the reference `p` is on the stack; the Point object is on the heap
}
```

**Q: How does GC decide what to collect?**
Objects unreachable from GC roots (stack, statics, JNI) are eligible. Generational:
young (minor GC) vs old (major GC).

```
GC roots: local variables on the stack, static fields, active JNI references
   -> anything reachable by following references from a root survives
   -> everything else is garbage, eligible for collection

Eden -> Survivor (minor GC, frequent, cheap) -> Old Gen (major/full GC, rarer, expensive)
```

**Q: Can Java have memory leaks?**
Yes — unintended reachability (static caches, unremoved listeners, `ThreadLocal`s).

```java
class Cache {
    static final Map<String, byte[]> entries = new HashMap<>();   // static -> reachable forever
    void put(String key, byte[] value) { entries.put(key, value); }  // never evicted -> leak
}
```

**Q: What is the JIT?**
Runtime compiler turning hot bytecode into optimized native code; tiered C1/C2.

```
interpreter (slow, starts instantly)
  -> C1 "client" compiler kicks in on warm methods (fast to compile, some optimization)
    -> C2 "server" compiler kicks in on hot methods (slow to compile, heavily optimized)
```

**Q: JDK vs JRE vs JVM?**
JVM runs bytecode. JRE = JVM + core libs. JDK = JRE + compiler/tools (`javac`, `jar`).

```
JDK = JRE + javac, jar, javadoc, jdb            (what you install to develop)
JRE = JVM + core class libraries (java.lang, java.util, ...)   (what you need to run)
JVM = the bytecode interpreter/JIT itself
```

**Q: `StackOverflowError` vs `OutOfMemoryError`?**
`StackOverflowError`: a thread's fixed-size call stack is exhausted — almost always
uncontrolled recursion. `OutOfMemoryError`: the heap/metaspace can't satisfy an allocation
even after a full GC. Both are `Error`, not `Exception` — technically catchable, rarely
worth catching.

**Q: What are the class-loading phases, in order?**
Loading (find bytecode, create the `Class` object) → Linking (Verify bytecode safety,
Prepare/zero static fields, Resolve symbolic references) → Initialization (run `static`
initializers, exactly once, on first active use — not on mere reference).

```java
class Lazy { static { System.out.println("init"); } }
Lazy.class.getName();   // does NOT trigger initialization — just reflection metadata
new Lazy();               // NOW it initializes, exactly once, ever
```

## Modern Java (11 → 21)

**Q: What do records give you for free?**
Constructor, accessors (no `get` prefix), `equals`/`hashCode`, `toString` — value
semantics from one line. Implicitly `final`; can't extend another class.

```java
record Range(int low, int high) {}
// the compiler generates:
//   Range(int low, int high) { this.low = low; this.high = high; }
//   int low() / int high()
//   equals(), hashCode() based on both fields
//   toString() -> "Range[low=1, high=5]"
```

**Q: What's a compact canonical constructor on a record?**
A constructor with no parameter list (`Range { ... }`) used to validate/normalize fields
before the implicit field assignment runs.

```java
record Range(int low, int high) {
    Range {                                    // no parameter list — "compact" form
        if (low > high) throw new IllegalArgumentException("low > high");
    }                                           // `this.low = low; this.high = high;` still runs after
}
```

**Q: What do sealed types buy you?**
A closed, compiler-known set of subtypes (`permits`) — enables exhaustive `switch`
pattern matching with no `default` branch, algebraic-data-type-style modeling.

```java
sealed interface Shape permits Circle, Square {}
record Circle(double radius) implements Shape {}
record Square(double side) implements Shape {}

double area(Shape s) {
    return switch (s) {                        // exhaustive — no `default` needed
        case Circle c -> Math.PI * c.radius() * c.radius();
        case Square sq -> sq.side() * sq.side();
    };
}
```

**Q: Pattern matching for switch + records — what's the C++ analog?**
Java's answer to `std::variant` + `std::visit`: an exhaustive, deconstructing switch over
a sealed hierarchy, with e.g. `case Rectangle(double w, double h) -> ...` binding a
record's components directly.

```java
switch (shape) {
    case Circle(double r) -> System.out.println("circle, r=" + r);
    case Square(double side) -> System.out.println("square, side=" + side);
}   // `r` and `side` are bound straight out of the record's components — no accessor calls
```

**Q: Does `var` make Java dynamically typed?**
No — local type inference only (like C++ `auto`); the compile-time type is still fixed
and fully static. Not allowed for fields, parameters, or return types.

```java
var list = new ArrayList<String>();   // inferred once, as ArrayList<String> — fixed forever
list.add("ok");
// list.add(1);           // compile error — still strongly, statically typed

// illegal uses of var:
// class Foo { var x; }              // no var fields
// void method(var x) { }            // no var parameters
// var method() { return 1; }        // no var return types
```

**Q: `instanceof` pattern matching — what changed vs the old idiom?**
`if (obj instanceof String s)` casts and binds `s` in one step, scoped to where the check
is true — replaces `if (obj instanceof String) { String s = (String) obj; }`.

```java
// old idiom
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// pattern matching
if (obj instanceof String s) {
    System.out.println(s.length());   // `s` is already cast and in scope here
}
```

## Design patterns

Runnable end-to-end examples for every pattern below live in
[11-design-patterns](../11-design-patterns/) — the snippets here are just the one line
that decides "which pattern is this."

**Q: Why is enum the recommended Singleton implementation?**
Free serialization-safety and reflection-safety (a normal class's private constructor
can still be invoked via reflection; enums can't be instantiated that way). Also
thread-safe by construction — no double-checked locking needed.

```java
enum Config {
    INSTANCE;
    private final Map<String, String> values = new HashMap<>();
}
Config.INSTANCE.values.get("key");   // JVM guarantees exactly one instance, ever
```
See [Singleton.java](../11-design-patterns/Singleton.java).

**Q: Why does double-checked-locking Singleton need `volatile`?**
Without it, the JMM permits instruction reordering that lets another thread observe a
reference to a *partially constructed* object — the field write can appear to happen
before the constructor finishes.

```java
class Singleton {
    private static volatile Singleton instance;   // volatile is load-bearing here
    static Singleton getInstance() {
        if (instance == null) {                     // 1st check, no lock — fast path
            synchronized (Singleton.class) {
                if (instance == null) {              // 2nd check, under lock
                    instance = new Singleton();       // without `volatile`, this write can reorder
                }
            }
        }
        return instance;
    }
}
```

**Q: Builder vs telescoping constructors?**
Java has no named/default parameters, so multiple overloaded constructors
(`Foo(a)`, `Foo(a,b)`, `Foo(a,b,c)`) get unreadable fast. A fluent builder scales to many
optional fields and can validate at `build()` time.

```java
Pizza p = new Pizza.Builder()
    .size(12)
    .topping("mushroom")
    .topping("olive")
    .build();   // vs. new Pizza(12, true, false, true, null, ...) with 6+ boolean flags
```
See [Builder.java](../11-design-patterns/Builder.java).

**Q: Decorator vs inheritance?**
Decorator composes behavior at runtime by wrapping the same interface (like `java.io`
stream classes); avoids a combinatorial explosion of subclasses for every combination
of behaviors.

```java
InputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream("f.gz")));
// each layer wraps the same InputStream interface and adds one responsibility —
// no BufferedGzipFileInputStream subclass required
```

**Q: How did Java 8 change the Strategy pattern?**
A single-method strategy interface can now be satisfied by a lambda instead of a named
class — `Comparator`, `Function`, `Predicate` are all "strategy" slots you fill inline.

```java
list.sort((a, b) -> a.length() - b.length());   // the "strategy" is just a lambda, no class needed
```

**Q: Adapter vs Decorator vs Facade — what's the difference?**
Adapter: makes an *incompatible* interface *compatible* (translates). Decorator: adds
behavior to a *compatible* interface (same shape, more responsibility). Facade:
simplifies a *complex subsystem* behind one easy interface (no wrapping of a single object).

```java
class LegacyLoggerAdapter implements Logger {         // Adapter: translate one shape to another
    private final LegacyLogger legacy;
    public void log(String msg) { legacy.writeToLog(msg); }
}
```

**Q: Proxy vs Decorator — same structure, different intent?**
Both wrap an object behind the same interface. Proxy controls *access* to the same
conceptual object (lazy load, security check, caching). Decorator adds *behavior*,
assuming access is already fine. Near-identical structurally; intent differs.

```java
class LazyImageProxy implements Image {
    private final String path;
    private Image real;                                // not loaded yet
    public void draw() {
        if (real == null) real = new RealImage(path);   // load on first access, not before
        real.draw();
    }
}
```

**Q: Factory Method in modern Java — do you still need a class hierarchy?**
Often not — a static method returning a sealed-interface type (module 08), with an
exhaustive `switch` over the concrete cases, replaces a hierarchy of factory subclasses.

```java
static Shape createShape(String kind, double size) {
    return switch (kind) {
        case "circle" -> new Circle(size);
        case "square" -> new Square(size);
        default -> throw new IllegalArgumentException(kind);
    };
}
```

**Q: Observer — does Java have built-in support?**
`java.util.Observable` exists but is deprecated/not recommended. In practice, hand-roll
a small listener-list implementation or reach for a framework (Spring events, RxJava).

```java
class Publisher {
    private final List<Consumer<String>> listeners = new ArrayList<>();
    void subscribe(Consumer<String> listener) { listeners.add(listener); }
    void publish(String event) { listeners.forEach(l -> l.accept(event)); }
}
```

**Q: Command — when is a plain lambda/`Runnable` enough instead of a Command class?**
When there's no need to queue, log, or undo — a fire-and-forget action can just be a
`Runnable`/lambda. A class earns its keep once it must hold state to reverse the action.

```java
Runnable saveFile = () -> save(document);        // fine — nothing to undo, nothing to queue

class MoveCommand {                                // earns a class — holds state to undo()
    private final Point from, to;
    void execute() { piece.moveTo(to); }
    void undo() { piece.moveTo(from); }
}
```

**Q: Template Method vs Strategy?**
Template Method fixes the algorithm's *shape* in a base class and lets subclasses
override individual steps (inheritance). Strategy swaps the *whole* algorithm at runtime
via composition (an injected interface or lambda).

```java
abstract class DataProcessor {                       // Template Method: shape is fixed
    final void process() { read(); transform(); write(); }   // subclasses fill in the steps
    abstract void transform();
}

class Sorter {                                        // Strategy: whole algorithm swapped in
    private final Comparator<Item> strategy;
    Sorter(Comparator<Item> strategy) { this.strategy = strategy; }
}
```

**Q: Iterator — what does the enhanced for-loop compile down to?**
`for (T x : xs)` calls `xs.iterator()` once, then loops on `hasNext()`/`next()`.
Implement `Iterable` yourself and you get that syntax for free.

```java
for (String x : list) { ... }
// desugars to:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    String x = it.next();
    ...
}
```

**Q: Composite — how does it relate to sealed types (module 08)?**
A `sealed interface ... permits Leaf, Composite` documents the closed set of node types
in the type system, letting an exhaustive `switch` stand in for virtual dispatch over
the same recursive tree.

```java
sealed interface Node permits Leaf, Branch {}
record Leaf(int value) implements Node {}
record Branch(List<Node> children) implements Node {}

int sum(Node n) {
    return switch (n) {
        case Leaf l -> l.value();
        case Branch b -> b.children().stream().mapToInt(this::sum).sum();
    };
}
```

**Q: Chain of Responsibility — real Java examples to cite?**
Servlet `FilterChain`, Spring Security's filter chain — each handler decides whether to
act and/or forward the request to the next one in line.

```java
abstract class Handler {
    protected Handler next;
    Handler setNext(Handler next) { this.next = next; return next; }
    void handle(Request r) {
        if (!canHandle(r) && next != null) next.handle(r);   // pass down the chain
    }
    abstract boolean canHandle(Request r);
}
```

## Testing

**Q: How do you unit test a class with dependencies?**
Inject dependencies (constructor injection) so they can be replaced with test doubles;
mock them with Mockito, stub the calls you need, and assert on the class under test's
behavior in isolation.

```java
class OrderService {
    private final PaymentGateway gateway;               // injected, not `new`'d internally
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }
    boolean placeOrder(Order o) { return gateway.charge(o.total()); }
}

@Test
void chargesGatewayForOrderTotal() {
    PaymentGateway mockGateway = mock(PaymentGateway.class);
    when(mockGateway.charge(100)).thenReturn(true);

    OrderService service = new OrderService(mockGateway);
    assertTrue(service.placeOrder(new Order(100)));
}
```

**Q: Mock vs Spy?**
Mock: a bare fake, everything returns null/0/false unless stubbed. Spy: wraps a *real*
object, real methods run unless you explicitly override them.

```java
List<String> mockList = mock(List.class);
mockList.size();                 // 0 — nothing is stubbed, mocks default to "empty"

List<String> spyList = spy(new ArrayList<>());
spyList.add("real");
spyList.size();                  // 1 — the real ArrayList.add()/size() ran
doReturn(99).when(spyList).size();  // override just this one call
```

**Q: Why prefer `assertThrows` over try/catch + fail()?**
Shorter, and it fails clearly if *no* exception is thrown (a bare try/catch that doesn't
call `fail()` in the try block can silently pass a test that never threw).

```java
// fragile: forgetting fail() here means the test passes even if nothing throws
try {
    service.divide(1, 0);
    fail("expected ArithmeticException");
} catch (ArithmeticException expected) { }

// preferred: fails automatically if no exception is thrown, and returns it for assertions
ArithmeticException ex = assertThrows(ArithmeticException.class, () -> service.divide(1, 0));
assertEquals("/ by zero", ex.getMessage());
```

**Q: What's a flaky test and what usually causes it?**
A test that passes/fails nondeterministically — usually real time (`Thread.sleep`,
`System.currentTimeMillis`), unmocked randomness, real network calls, or shared mutable
state leaking between tests (missing `@BeforeEach` reset).

```java
// flaky: races against a background thread instead of waiting for a deterministic signal
Thread.sleep(100);
assertTrue(cache.contains("key"));

// deterministic: inject a Clock/Random, or await a condition instead of a fixed sleep
Clock fixedClock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
```

## Spring / DI

**Q: What problem does Dependency Injection solve?**
Decouples an object from constructing its own dependencies — a container wires them in,
so implementations can be swapped (real vs mock) and configuration is centralized.

```java
class OrderService {
    private final PaymentGateway gateway = new StripeGateway();  // hardwired — hard to test/swap
}

class OrderService {
    private final PaymentGateway gateway;                         // injected — swap freely
    OrderService(PaymentGateway gateway) { this.gateway = gateway; }
}
```

**Q: Constructor injection vs field `@Autowired`?**
Constructor injection makes dependencies explicit and `final`, fails fast at startup if a
bean is missing, and works in plain unit tests without a Spring container at all.

```java
@Service
class OrderService {
    private final PaymentGateway gateway;          // final — impossible to forget to set

    OrderService(PaymentGateway gateway) {          // Spring injects automatically (single ctor)
        this.gateway = gateway;
    }
}
// vs field injection: @Autowired private PaymentGateway gateway;
// — not final, needs a Spring container even to unit test, fails at first *use*, not startup
```

**Q: `@Component` vs `@Bean`?**
`@Component` annotates a class you own, picked up by `@ComponentScan`. `@Bean` is a
factory method inside a `@Configuration` class — for wiring types you don't own/can't annotate.

```java
@Component                                    // class you own — auto-discovered
class OrderService { ... }

@Configuration
class AppConfig {
    @Bean                                     // type you don't own (e.g. a 3rd-party client)
    ObjectMapper objectMapper() { return new ObjectMapper(); }
}
```

**Q: What does `@SpringBootApplication` expand to?**
`@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.

```java
@SpringBootApplication
class App { public static void main(String[] a) { SpringApplication.run(App.class, a); } }
// equivalent to stacking:
// @Configuration @ComponentScan @EnableAutoConfiguration
```

**Q: Spring singleton scope vs the GoF Singleton pattern (module 11)?**
A Spring singleton bean is one instance **per container** (you can have multiple
containers, or refresh one), not one instance per JVM like the classic GoF pattern.

```java
ApplicationContext ctx1 = new AnnotationConfigApplicationContext(AppConfig.class);
ApplicationContext ctx2 = new AnnotationConfigApplicationContext(AppConfig.class);
ctx1.getBean(OrderService.class) == ctx2.getBean(OrderService.class);   // false — two containers
```

## Design / good taste

**Q: SOLID in one line each?**
Single responsibility, Open/closed, Liskov substitution, Interface segregation,
Dependency inversion.

```java
// S — one reason to change: split "parse the file" from "save to the database"
// O — open for extension: add a new Shape subtype, don't edit the area() switch everywhere
// L — subtypes are substitutable: a Square shouldn't break code written against Rectangle
// I — small, focused interfaces: don't force a class to implement methods it doesn't need
// D — depend on abstractions: `PaymentGateway` interface, not `StripeGateway` concretely
```

**Q: Favorite Java idioms you'd bring from C++ experience?**
Program to interfaces, immutability by default (records/`final`), `Optional` over null,
prefer composition over inheritance, streams for declarative transforms.

```java
List<String> names = List.of("a", "b");            // program to interface, immutable by default
Optional<User> user = repo.findById(id);            // Optional over raw null
class Car { private final Engine engine; }           // composition over inheritance
names.stream().map(String::toUpperCase).toList();    // declarative, not a manual for-loop
```

## Practice — answer these yourself, out loud, before checking

No answers given here on purpose — that's the point of rapid-fire prep. Say the bold-line
answer *and* sketch the code example from memory, then check yourself against the matching
module. If you can't reconstruct the example, you don't know it yet — just the sentence.

1. Why does `x == y` sometimes return `true` and sometimes `false` for two `Integer`s
   holding the same value? (module 01/09)
2. What happens if you call an overridable method from inside a constructor? (module 02)
3. Why can't you have `List<int>`? What three things does that break? (module 03)
4. What's the difference between what `ArrayList.remove(int)` and `ArrayList.remove(Object)`
   do, and why is `list.remove(1)` on a `List<Integer>` a classic gotcha? (module 03)
5. Why does a `return` inside `finally` silently swallow an exception thrown in `try`? (module 04)
6. What's the difference between `Files.lines(path)` and `Files.readAllLines(path)`, and
   when does the difference matter? (module 04)
7. Why is `flatMap` needed instead of just `map` for a `List<List<T>>`? (module 05)
8. Why might a `parallelStream()` make a small pipeline *slower*, not faster? (module 05)
9. Why does double-checked-locking Singleton break without `volatile`? (module 06/11)
10. What's the actual difference between `wait()`/`notify()` and `Condition.await()`/`signal()`? (module 06)
11. Why doesn't escape analysis let you rely on objects "usually" being stack-allocated? (module 07)
12. What three things happen, in order, between `javac` and your `main` method running? (module 07)
13. Why is a `record`'s auto-generated `equals()` sometimes *wrong* for your use case —
    when would you override it by hand? (module 08)
14. What does `permits` buy you that a plain (non-sealed) interface doesn't? (module 08)
15. When would you choose Composite over just using a `List<Node>` directly? (module 11)
16. Why does over-mocking (6+ mocked collaborators in one test) usually mean a design
    problem, not a testing problem? (module 12)
17. Why does constructor injection make a class testable *without* starting a Spring
    context at all? (module 13)
18. Why does Java 25 `synchronized` code no longer have the broad virtual-thread pinning
    warning from early Java-21 material, and which pinning cases remain? (module 06)
19. When is a scoped value safer than a `ThreadLocal`, and when is an explicit parameter
    clearer than either? (module 06/08)
20. A client times out after `POST /orders`: why is the outcome unknown and how does an
    idempotency record make the retry safe? (module 20/22)
21. How do an optimistic-lock version and an idempotency key solve different races? (module 17/20)
22. Why does a transactional outbox tolerate broker failure without making publication
    exactly-once, and what makes the consumer effect idempotent? (module 20/22)
23. Which evidence would you collect before changing GC flags for rising p99 latency? (module 21)
