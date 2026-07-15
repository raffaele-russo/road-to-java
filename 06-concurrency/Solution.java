import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/** Reference solution for Exercise.java. */
public class Solution {
    static int safeIncrement(int threads, int increments) throws InterruptedException {
        AtomicInteger count = new AtomicInteger();
        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread worker = new Thread(() -> {
                for (int j = 0; j < increments; j++) count.incrementAndGet();
            });
            workers.add(worker);
            worker.start();
        }
        for (Thread worker : workers) worker.join();
        return count.get();
    }

    static long sumViaQueue(int producers, int itemsPerProducer) throws InterruptedException {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        List<Thread> workers = new ArrayList<>();
        for (int p = 0; p < producers; p++) {
            Thread worker = new Thread(() -> {
                try {
                    for (int item = 1; item <= itemsPerProducer; item++) queue.put(item);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            workers.add(worker);
            worker.start();
        }
        long sum = 0;
        for (int i = 0; i < producers * itemsPerProducer; i++) sum += queue.take();
        for (Thread worker : workers) worker.join();
        return sum;
    }

    public static void main(String[] args) throws Exception {
        assert safeIncrement(4, 10_000) == 40_000;
        assert sumViaQueue(2, 100) == 10_100;
        System.out.println("Module 06 reference solution passed.");
    }
}
