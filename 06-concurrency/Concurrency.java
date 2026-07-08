import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * Executors, the race-condition problem, atomics vs unsynchronized ++,
 * CompletableFuture, and virtual threads (Java 21).
 * Run:  java 06-concurrency/Concurrency.java
 */
public class Concurrency {

    public static void main(String[] args) throws Exception {
        executorBasics();
        raceConditionDemo();
        completableFuture();
        virtualThreads();
    }

    static void executorBasics() throws Exception {
        System.out.println("== executor + Future ==");
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            Future<Integer> f = pool.submit(() -> {
                Thread.sleep(50);
                return 21 * 2;
            });
            System.out.println("  future result: " + f.get());
        }
    }

    // Demonstrates why count++ needs synchronization: race vs AtomicInteger.
    static void raceConditionDemo() throws Exception {
        System.out.println("\n== race condition: plain int vs AtomicInteger ==");
        final int threads = 8, perThread = 10_000;
        final int expected = threads * perThread;

        int[] unsafe = {0};                       // NOT thread-safe
        AtomicInteger safe = new AtomicInteger();

        try (ExecutorService pool = Executors.newFixedThreadPool(threads)) {
            CountDownLatch done = new CountDownLatch(threads);
            for (int t = 0; t < threads; t++) {
                pool.submit(() -> {
                    for (int i = 0; i < perThread; i++) {
                        unsafe[0]++;              // data race — lost updates
                        safe.incrementAndGet();   // atomic — always correct
                    }
                    done.countDown();
                });
            }
            done.await();
        }
        System.out.println("  expected      : " + expected);
        System.out.println("  unsafe int++  : " + unsafe[0] + "  (usually < expected — lost updates)");
        System.out.println("  AtomicInteger : " + safe.get() + "  (always correct)");
    }

    static void completableFuture() throws Exception {
        System.out.println("\n== CompletableFuture chaining ==");
        String result = CompletableFuture
            .supplyAsync(() -> "data")
            .thenApply(String::toUpperCase)
            .thenApply(s -> s + "!")
            .exceptionally(ex -> "fallback")
            .get();
        System.out.println("  pipeline result: " + result);
    }

    static void virtualThreads() throws Exception {
        System.out.println("\n== virtual threads (Java 21) ==");
        long start = System.currentTimeMillis();
        try (ExecutorService vt = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = IntStream.range(0, 10_000)
                .mapToObj(i -> vt.submit(() -> { Thread.sleep(10); return i; }))
                .toList();
            long sum = 0;
            for (var f : futures) sum += f.get();
            System.out.println("  10,000 blocking tasks done, sum=" + sum);
        }
        System.out.println("  elapsed: " + (System.currentTimeMillis() - start) + "ms "
            + "(would be ~minutes with 10k platform threads)");
    }
}
