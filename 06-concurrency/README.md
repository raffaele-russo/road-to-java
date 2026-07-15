# 06 — Concurrency

## Learning outcome and prerequisite

**Outcome:** Prove visibility and atomicity with happens-before and implement cooperative cancellation.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

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

Virtual threads improve **throughput for blocking I/O**, not CPU speed. Do not pool them to limit
concurrency; use a semaphore or bounded resource pool for the scarce dependency. They carry the
same data-race rules as platform threads. Since Java 24, blocking inside `synchronized` no longer
causes the broad carrier-thread pinning limitation taught by early Java-21 material; remaining
native/class-initialization pinning can be inspected with JFR. See
[JEP 491](https://openjdk.org/jeps/491).

## Cancellation and interruption

Cancellation is cooperative. `interrupt()` requests that a thread stop waiting/work; it does not
safely kill the thread. Blocking JDK operations commonly throw `InterruptedException`. Either let
it propagate or restore the flag when translating it:

```java
try {
    queue.take();
} catch (InterruptedException cancelled) {
    Thread.currentThread().interrupt();
    return;                         // unwind and release resources
}
```

Loops performing CPU work must periodically check `Thread.currentThread().isInterrupted()`.
Cancellation must flow downward to child tasks, and the caller must still join/close their scope.
Never swallow interruption and continue as if work completed.

## Scoped values and structured concurrency (Java 25)

Scoped values are final in Java 25: they share immutable, lexically bounded context with callees
and child threads and avoid the mutability/lifetime leaks of pooled-thread `ThreadLocal`. Prefer
ordinary parameters unless context must cross framework callbacks. See
[JEP 506](https://openjdk.org/jeps/506).

Structured concurrency treats related forked tasks as one lifetime: success waits for required
children; failure cancels siblings; leaving the scope joins/cancels remaining work. In Java 25 it
is still a preview API ([JEP 505](https://openjdk.org/jeps/505)), so this repository teaches the
model but does not require preview flags. `CompletableFuture` remains useful for externally owned
pipelines; a structured scope is easier to reason about for request-owned fan-out.

## Thread lifecycle states

`Thread.State`: `NEW` (created, not started) → `RUNNABLE` (running or ready to run — the
JVM doesn't distinguish "running" from "ready", that's the OS scheduler's job) →
`BLOCKED` (waiting on a monitor lock) / `WAITING` (indefinite, e.g. `Object.wait()` with
no timeout, or `join()`) / `TIMED_WAITING` (`sleep(ms)`, timed `wait`/`join`) →
`TERMINATED` (run completed or threw). Know these names — "what state is a thread in
while blocked on `synchronized`?" is a common quick-check question (`BLOCKED`).

## `Callable` vs `Runnable`

```java
Runnable r = () -> System.out.println("no result, no checked exception");
Callable<Integer> c = () -> { return 42; };   // can return a value AND throw a checked exception

ExecutorService pool = Executors.newFixedThreadPool(2);
Future<Integer> f = pool.submit(c);           // submit(Runnable) also works, Future<?> result is null
```

## Shutting down an `ExecutorService` correctly

```java
pool.shutdown();                              // stop accepting new tasks, let queued ones finish
if (!pool.awaitTermination(5, TimeUnit.SECONDS)) {
    pool.shutdownNow();                       // interrupt running tasks, cancel queued ones
}
```
`shutdown()` is graceful; `shutdownNow()` interrupts. Forgetting to shut down a pool at
all is a classic resource leak — non-daemon pool threads keep the JVM alive.

## `ThreadLocal` — per-thread state without synchronization

Gives each thread its own independent copy of a variable — useful for things like a
per-request context or a non-thread-safe object (e.g. `SimpleDateFormat`) you don't want
to synchronize on.

```java
private static final ThreadLocal<SimpleDateFormat> FORMAT =
    ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
FORMAT.get().format(new Date());     // each thread gets its own formatter instance
```
**Leak warning:** in a pooled-thread environment (executors, servlet containers), call
`.remove()` when done — the pool reuses the thread, so a forgotten `ThreadLocal` value
outlives the logical task and can leak memory or leak data between requests.

## Producer-consumer with `BlockingQueue`

The idiomatic way to hand work between threads without manual `wait`/`notify`:

```java
BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
// producer
queue.put(item);              // blocks if the queue is full
// consumer
int item = queue.take();      // blocks if the queue is empty
```

## Deadlock vs livelock vs starvation

- **Deadlock**: threads block each other forever, each holding a resource the other needs.
- **Livelock**: threads keep changing state in response to each other but make no
  progress (e.g. both politely "yield" to avoid a collision, forever).
- **Starvation**: a thread never gets CPU time or a lock because others are always
  prioritized (e.g. an unfair lock, or lower-priority threads never scheduled).

## Interview one-liners
- `start()` vs `run()`: `start()` spawns a thread; `run()` runs on the caller.
- Why `synchronized` over `volatile`: volatile gives visibility, not atomicity.
- `wait/notify` must be called while holding the object's monitor, in a loop guarding the condition.
- Prefer higher-level utilities (executors, concurrent collections, atomics) over raw `synchronized`/`wait`.

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). Deterministic (no timing-dependent asserts —
everything synchronizes via `join`/`BlockingQueue` so it's reliable to run repeatedly):

1. `raceyIncrement(int threads, int incrementsPerThread)` — spins up threads that all
   `count++` a **shared, non-atomic** `int` with no synchronization, joins them, and
   returns the final count. Just run it (it's already implemented) and see it's usually
   *wrong* — this demonstrates the race, it's not something to "fix" in place.
2. `safeIncrement(int threads, int incrementsPerThread)` — TODO: same shape as above, but
   using `AtomicInteger` so the result is always exactly `threads * incrementsPerThread`.
3. `sumViaQueue(int producers, int itemsPerProducer)` — TODO: spin up `producers` threads
   that each `put` the numbers `1..itemsPerProducer` onto a shared `BlockingQueue<Integer>`,
   plus one consumer thread that `take`s until it has seen
   `producers * itemsPerProducer` items and sums them. Join everything, return the sum.

```bash
java -ea 06-concurrency/Exercise.java
```

## Run

```bash
java 06-concurrency/Concurrency.java
```

## Retrieval practice, hints, and solution

1. Name the happens-before edge that makes a value visible.
2. Why does interruption require cooperation?
3. When do virtual threads help and when do they not?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
