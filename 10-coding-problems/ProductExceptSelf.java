// Practice (unsolved): return an array where result[i] = product of every element
// except nums[i], WITHOUT using division and in O(n) time.
// Pattern: prefix products left-to-right, then fold in suffix products right-to-left,
// reusing the output array as the accumulator — O(1) extra space beyond the output.
// Run once you think you're done:  java -ea 10-coding-problems/ProductExceptSelf.java
public class ProductExceptSelf {

    /** productExceptSelf({1,2,3,4}) -> {24,12,8,6} */
    static int[] productExceptSelf(int[] nums) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        int[] r1 = productExceptSelf(new int[]{1, 2, 3, 4});
        assert java.util.Arrays.equals(r1, new int[]{24, 12, 8, 6}) : "got: " + java.util.Arrays.toString(r1);

        int[] r2 = productExceptSelf(new int[]{2, 3});
        assert java.util.Arrays.equals(r2, new int[]{3, 2}) : "got: " + java.util.Arrays.toString(r2);

        // contains a zero: every OTHER position becomes the product of the rest, the zero's
        // own position becomes the product of everything except itself (i.e. the nonzero part)
        int[] r3 = productExceptSelf(new int[]{1, 0, 3});
        assert java.util.Arrays.equals(r3, new int[]{0, 3, 0}) : "got: " + java.util.Arrays.toString(r3);

        System.out.println("All good — ProductExceptSelf complete.");
    }
}
