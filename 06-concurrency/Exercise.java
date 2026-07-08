import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

// Practice: implement safeIncrement and sumViaQueue below. raceyIncrement is already
// implemented as a demonstration — run it and watch it usually print the WRONG total.
// Run once you think you're done:  java -ea 06-concurrency/Exercise.java
public class Exercise {

    /** Already implemented — a deliberately broken demonstration of a data race.
     *  count++ is read-modify-write, not atomic, so concurrent threads lose updates. */
    static int raceyIncrement(int threads, int incrementsPerThread) throws InterruptedException {
        int[] count = {0};   // boxed in an array just so the lambda below can mutate it
        List<Thread> pool = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) count[0]++;   // NOT thread-safe
            });
            pool.add(t);
            t.start();
        }
        for (Thread t : pool) t.join();
        return count[0];
    }

    /** TODO: same shape as raceyIncrement, but using AtomicInteger so the result is
     *  always exactly threads * incrementsPerThread, every single run. */
    static int safeIncrement(int threads, int incrementsPerThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO");
    }

    /** TODO: start `producers` threads; each puts the integers 1..itemsPerProducer (inclusive)
     *  onto a shared BlockingQueue<Integer>. Meanwhile the CURRENT (main) thread acts as the
     *  consumer: take() until you've received producers * itemsPerProducer items total,
     *  summing them as you go. Join the producer threads before returning. Use a queue
     *  capacity smaller than the total item count (e.g. 10) so put()/take() actually block
     *  and hand off work, rather than everything fitting in an unbounded buffer at once. */
    static long sumViaQueue(int producers, int itemsPerProducer) throws InterruptedException {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) throws InterruptedException {
        int racey = raceyIncrement(8, 100_000);
        System.out.println("raceyIncrement result (usually wrong): " + racey + " (expected 800000)");

        int safe = safeIncrement(8, 100_000);
        assert safe == 800_000 : "safeIncrement must always be exact, got " + safe;

        long sum = sumViaQueue(4, 1000);
        long expected = 4L * (1000L * 1001L / 2L);   // 4 producers * sum(1..1000)
        assert sum == expected : "sumViaQueue got " + sum + ", expected " + expected;

        System.out.println("All good — module 06 exercise complete.");
    }
}
