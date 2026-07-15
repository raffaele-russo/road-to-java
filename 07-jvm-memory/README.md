# 07 — JVM & Memory Management

## Learning outcome and prerequisite

**Outcome:** Relate runtime memory, GC, class loading, and JIT behavior to diagnostic evidence.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

Where a C++ dev's instincts are most different. You don't manage memory, but you must
understand what the JVM does so you can reason about performance and leaks.

## Compilation & execution pipeline

```
.java  --javac-->  .class (bytecode)  --JVM-->  interpreted, then JIT-compiled to native
```

- **Bytecode** is portable ("write once, run anywhere"). The JVM is the abstraction layer.
- **JIT (Just-In-Time) compiler** profiles running code and compiles hot paths to native
  machine code at runtime — often reaching C++-comparable speed for hot loops.
- **Tiered compilation**: interpreter → C1 (fast, less optimized) → C2 (slow, aggressive).

## Runtime memory areas

| Area | Holds | Shared? |
|------|-------|---------|
| **Heap** | all objects & arrays | yes (all threads) — GC works here |
| **Stack** | per-thread frames: locals, operand stack, primitive values | per-thread |
| **Metaspace** | class metadata (was "PermGen" pre-Java 8) | yes; native memory |
| **PC register** | current instruction per thread | per-thread |
| **Native method stack** | JNI calls | per-thread |

Key point vs C++: **objects are always on the heap**; the stack holds primitives and
*references*. There's no stack-allocated object, no `new`/`delete` pairing to track.

## Garbage collection

The GC automatically reclaims objects that are no longer **reachable** from GC roots
(stack variables, static fields, etc.). No `delete`, no dangling pointers, no double-free.

### Generational hypothesis
Most objects die young. So the heap is split:
- **Young generation** (Eden + two Survivor spaces): new objects. Minor GC is frequent
  and cheap; survivors are promoted.
- **Old (Tenured) generation**: long-lived objects. Major/Full GC is rarer, more expensive.

### Collectors (know the names)
| Collector | Trait |
|-----------|-------|
| **G1** (default since Java 9) | region-based, balances throughput & pause time |
| **Parallel** | max throughput, longer pauses (batch jobs) |
| **ZGC / Shenandoah** | ultra-low pause (<1ms), large heaps |
| **Serial** | single-threaded, tiny heaps/containers |

Tune with flags: `-Xms`/`-Xmx` (initial/max heap), `-XX:+UseZGC`, etc.

## Memory leaks still happen (yes, with a GC)

Reachable-but-unused objects are never collected. Classic causes:
- Objects lingering in a **static** collection / cache that's never cleared.
- Listeners/callbacks registered but never removed.
- `ThreadLocal`s not cleaned up in pooled threads.
- Keys in a `HashMap` with broken `equals`/`hashCode`.

Fix with `WeakReference`/`WeakHashMap` for caches, and by clearing references you no
longer need.

## References: strong / soft / weak / phantom
- **Strong** (normal): never collected while reachable.
- **Soft**: collected only under memory pressure — good for caches.
- **Weak**: collected at next GC if only weakly reachable — `WeakHashMap`.
- **Phantom**: for post-mortem cleanup, replaces `finalize()`.

## `final`, immutability, and escape analysis
- Immutable objects are inherently thread-safe and GC-friendly.
- **Escape analysis** may let the JIT stack-allocate or scalar-replace objects that don't
  escape a method — so `new` in a tight loop isn't always a heap allocation.

## Class loading
- Classes load lazily on first use via a hierarchy of **class loaders** (Bootstrap →
  Platform → Application). `static` initializers run once, at class init time.
- Three phases, in order: **Loading** (find the bytecode, create a `Class` object),
  **Linking** — itself three sub-steps: **Verify** (bytecode is valid/safe), **Prepare**
  (allocate static fields, zero them), **Resolve** (symbolic references → direct
  references), then **Initialization** (run `static` initializers/blocks, top to bottom,
  exactly once, triggered by first active use — `new`, a static method call, a static
  field access that isn't a compile-time constant).

```java
class Lazy {
    static { System.out.println("Lazy class initialized"); }  // runs once, on first real use
}
// nothing printed yet — merely mentioning the class name (e.g. in a comment) doesn't trigger it
Lazy.class.getName();     // still doesn't trigger initialization — reflection metadata only
new Lazy();                // NOW "Lazy class initialized" prints, exactly once ever
new Lazy();                // no output — already initialized
```

## `StackOverflowError` vs `OutOfMemoryError`
- **`StackOverflowError`**: a thread's call stack (fixed size, see `-Xss`) is exhausted —
  almost always uncontrolled/too-deep recursion (missing or wrong base case).
- **`OutOfMemoryError`**: the heap (or metaspace) can't satisfy an allocation even after a
  full GC — too many live objects, or a genuine leak (module 07's reachability leaks).
Both are subclasses of `Error`, not `Exception` — technically catchable, but you almost
never should (the JVM may already be in a bad state, especially for OOM).

## Useful JVM flags to know by name (not memorize syntax)

| Flag | Purpose |
|------|---------|
| `-Xms` / `-Xmx` | initial / maximum heap size |
| `-Xss` | per-thread stack size (tune before "fixing" a StackOverflowError by adding memory) |
| `-XX:+UseG1GC` / `-XX:+UseZGC` | pick a garbage collector |
| `-XX:+HeapDumpOnOutOfMemoryError` | dump the heap for post-mortem analysis on OOM |
| `-verbose:class` | log every class as it's loaded — see the lazy-loading behavior live |

## Profiling & diagnostic tools worth naming in an interview
`jstack` (thread dumps — diagnose deadlocks/hangs), `jmap`/`jcmd` (heap dumps/histograms),
`jconsole`/`VisualVM`/Java Flight Recorder (live monitoring, allocation profiling), and
`async-profiler` for low-overhead production CPU/allocation profiling.

## Interview one-liners
- Stack vs heap: primitives/refs on stack, objects on heap.
- Why no destructors: non-deterministic GC; use `try-with-resources`/`AutoCloseable`.
- Can Java leak memory? Yes — unintended reachability.
- What are GC roots? Live stack refs, static fields, JNI refs.
- What is the JIT? Runtime native compilation of hot bytecode.

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). Deterministic experiments about JVM behavior:

1. `depthAtStackOverflow()` — TODO: recurse with an accumulating depth counter until a
   `StackOverflowError` is thrown, catch it, and return the depth reached. No exact
   number is asserted (it depends on the machine/thread stack size) — just that it's
   large and positive, proving the stack is finite and the error is catchable.
2. `initCount` / `touchLazy()` — TODO: demonstrate that a class's `static` initializer
   runs exactly once. `Lazy` and its counter are given; implement `touchLazy()` to
   trigger initialization by actually instantiating it. (`main` deliberately never reads
   `Lazy.initCount` before the first `touchLazy()` call — merely reading a non-constant
   static field is itself a triggering event, so checking "is it 0 yet?" would trigger
   the very initialization being observed.)
3. `identityVsEquality()` — TODO: return a 3-element boolean array proving: (a) two
   equal-but-distinct `Point` records have *different* `System.identityHashCode`s, (b)
   they *do* have equal `.hashCode()` (records generate value-based hashing), (c) the
   same reference has a stable `identityHashCode` across two calls.

```bash
java -ea 07-jvm-memory/Exercise.java
```

## Inspect your own runtime

```bash
java 07-jvm-memory/JvmInfo.java
java -XX:+PrintFlagsFinal -version | grep -i gc   # see GC flags
```

## Retrieval practice, hints, and solution

1. Which evidence separates a leak from a high allocation rate?
2. Why may equal identity-hash values still denote distinct objects?
3. What triggers class initialization?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
