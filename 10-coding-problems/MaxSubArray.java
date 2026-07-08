// Practice (unsolved): find the contiguous subarray with the largest sum, return the sum.
// Pattern: Kadane's algorithm — one pass, at each index decide "extend the running sum,
// or start fresh here" (dp[i] = max(nums[i], dp[i-1] + nums[i])), track the running max.
// O(n) time, O(1) space — a good example of DP collapsed into a running variable.
// Run once you think you're done:  java -ea 10-coding-problems/MaxSubArray.java
public class MaxSubArray {

    /** maxSubArray({-2,1,-3,4,-1,2,1,-5,4}) -> 6  (subarray [4,-1,2,1]) */
    static int maxSubArray(int[] nums) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        assert maxSubArray(new int[]{-2, 1, -3, 4, -1, 2, 1, -5, 4}) == 6;
        assert maxSubArray(new int[]{1}) == 1;
        assert maxSubArray(new int[]{5, 4, -1, 7, 8}) == 23 : "whole array is the best subarray here";
        assert maxSubArray(new int[]{-3, -1, -2}) == -1 : "all-negative: must still pick a non-empty subarray";

        System.out.println("All good — MaxSubArray complete.");
    }
}
