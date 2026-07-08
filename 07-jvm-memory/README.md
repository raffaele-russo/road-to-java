# 07 — JVM & Memory Management

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

## Interview one-liners
- Stack vs heap: primitives/refs on stack, objects on heap.
- Why no destructors: non-deterministic GC; use `try-with-resources`/`AutoCloseable`.
- Can Java leak memory? Yes — unintended reachability.
- What are GC roots? Live stack refs, static fields, JNI refs.
- What is the JIT? Runtime native compilation of hot bytecode.

## Inspect your own runtime

```bash
java 07-jvm-memory/JvmInfo.java
java -XX:+PrintFlagsFinal -version | grep -i gc   # see GC flags
```
