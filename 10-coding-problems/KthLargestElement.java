import java.util.PriorityQueue;

// Practice (unsolved): find the kth largest element in an unsorted array (kth largest
// in SORTED order, not the kth distinct value).
// Pattern: a min-heap of size k — push every element, pop whenever size exceeds k, and
// the heap's root at the end is the answer. O(n log k), no full sort needed.
// Run once you think you're done:  java -ea 10-coding-problems/KthLargestElement.java
public class KthLargestElement {

    /** findKthLargest({3,2,1,5,6,4}, 2) -> 5 (the 2nd largest value) */
    static int findKthLargest(int[] nums, int k) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        assert findKthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2) == 5;
        assert findKthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4) == 4;
        assert findKthLargest(new int[]{1}, 1) == 1;
        assert findKthLargest(new int[]{7, 6, 5, 4, 3, 2, 1}, 1) == 7 : "k=1 is just the max";

        System.out.println("All good — KthLargestElement complete.");
    }
}
