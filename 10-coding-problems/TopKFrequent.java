import java.util.*;

/**
 * Top K Frequent Elements.
 * Pattern: merge() to count, then a size-k min-heap (PriorityQueue) so we keep only the
 * k most frequent. O(n log k) time, O(n) space.
 * Run:  java 10-coding-problems/TopKFrequent.java
 */
public class TopKFrequent {

    static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int n : nums) freq.merge(n, 1, Integer::sum);

        // Min-heap ordered by frequency; evict the smallest when size exceeds k.
        PriorityQueue<Integer> heap =
            new PriorityQueue<>(Comparator.comparingInt(freq::get));
        for (int key : freq.keySet()) {
            heap.offer(key);
            if (heap.size() > k) heap.poll();              // drop least frequent
        }

        int[] result = new int[k];
        for (int i = k - 1; i >= 0; i--) result[i] = heap.poll(); // most frequent last out
        return result;
    }

    public static void main(String[] args) {
        int[] r = topKFrequent(new int[]{1, 1, 1, 2, 2, 3}, 2);
        System.out.println("top 2: " + Arrays.toString(r));   // [1, 2]
        assert r[0] == 1 && r[1] == 2 : "expected [1, 2]";

        int[] r2 = topKFrequent(new int[]{4, 4, 4, 5, 5, 6, 7, 7, 7, 7}, 1);
        System.out.println("top 1: " + Arrays.toString(r2));  // [7]
        assert r2[0] == 7;

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
