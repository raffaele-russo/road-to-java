# 06 — Concurrency

Java has a mature, standardized concurrency model (unlike C++ where it arrived late).
Interviewers love this area — know the memory model, not just the APIs.

## Threads — don't create them directly

```java
Thread t = new Thread(() -> work());
t.start();          // start() spawns; run() would just call it on the current thread
t.join();           // wait for completion
```

But raw threads are expensive. **Use an `ExecutorService`** (a thread pool):

```java
try (ExecutorService pool = Executors.newFixedThreadPool(4)) {  // AutoCloseable, Java 19+
    Future<Integer> f = pool.submit(() -> compute());
    int result = f.get();   // blocks for the result
}   // pool shut down automatically
```

Factory methods: `newFixedThreadPool`, `newCachedThreadPool`,
`newVirtualThreadPerTaskExecutor` (Java 21 — cheap virtual threads),
`newScheduledThreadPool`.

## The Java Memory Model (JMM) — the concept that separates seniors

Without synchronization, a write by one thread is **not guaranteed** to be visible to
another (threads may cache values, compiler/CPU may reorder). Two guarantees fix this:

- **`synchronized`** — mutual exclusion *and* a memory barrier (happens-before). Only one
  thread holds a monitor at a time; entering/leaving flushes/refreshes memory.
- **`volatile`** — no mutual exclusion, but guarantees visibility and prevents reordering
  of that field. Good for flags, not for compound actions (`count++` is not atomic).

```java
private volatile boolean running = true;    // visibility for a stop flag
public synchronized void inc() { count++; } // atomicity + visibility
```

**happens-before** is the key phrase: unlock happens-before subsequent lock; volatile
write happens-before subsequent volatile read; `Thread.start()` happens-before the
thread's run; thread's actions happen-before `join()` returns.

## Race conditions, atomics, locks

- `count++` is read-modify-write → not atomic → data race.
- **Atomics**: `AtomicInteger`, `AtomicLong`, `AtomicReference` — lock-free CAS.
- **`java.util.concurrent.locks.ReentrantLock`** — like `synchronized` but with
  `tryLock`, timeouts, fairness, and multiple `Condition`s.
- **Deadlock**: two threads acquire two locks in opposite order → both wait forever.
  Avoid by ordering lock acquisition consistently.

```java
AtomicInteger counter = new AtomicInteger();
counter.incrementAndGet();      // atomic, no lock
```

## Concurrent collections

Never share a plain `HashMap`/`ArrayList` across threads without external sync.

| Need | Use |
|------|-----|
| Concurrent map | `ConcurrentHashMap` |
| Producer/consumer queue | `BlockingQueue` (`ArrayBlockingQueue`, `LinkedBlockingQueue`) |
| Copy-on-write list (read-heavy) | `CopyOnWriteArrayList` |
| Counting/latching | `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `Phaser` |

## High-level async: `CompletableFuture`

```java
CompletableFuture.supplyAsync(() -> fetch())
    .thenApply(data -> transform(data))
    .thenAccept(System.out::println)
    .exceptionally(ex -> { log(ex); return null; });
```

## Virtual threads (Java 21) — the headline feature

Lightweight threads managed by the JVM; you can have millions. Write simple blocking
code that scales like async. Great for I/O-bound servers.

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 10_000; i++) executor.submit(() -> handle());
}
```

## Interview one-liners
- `start()` vs `run()`: `start()` spawns a thread; `run()` runs on the caller.
- Why `synchronized` over `volatile`: volatile gives visibility, not atomicity.
- `wait/notify` must be called while holding the object's monitor, in a loop guarding the condition.
- Prefer higher-level utilities (executors, concurrent collections, atomics) over raw `synchronized`/`wait`.

## Run

```bash
java 06-concurrency/Concurrency.java
```
