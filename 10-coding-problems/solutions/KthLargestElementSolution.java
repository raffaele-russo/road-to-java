import java.util.PriorityQueue;
public class KthLargestElementSolution {
    static int findKthLargest(int[] nums, int k) {
        if (k < 1 || k > nums.length) throw new IllegalArgumentException("k out of range");
        PriorityQueue<Integer> heap = new PriorityQueue<>();
        for (int value : nums) { heap.offer(value); if (heap.size() > k) heap.poll(); }
        return heap.element();
    }
    public static void main(String[] args) { assert findKthLargest(new int[]{3,2,1,5,6,4}, 2) == 5; }
}
