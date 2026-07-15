public class MaxSubArraySolution {
    static int maxSubArray(int[] nums) {
        if (nums.length == 0) throw new IllegalArgumentException("array must not be empty");
        int endingHere = nums[0], best = nums[0];
        for (int i = 1; i < nums.length; i++) {
            endingHere = Math.max(nums[i], endingHere + nums[i]);
            best = Math.max(best, endingHere);
        }
        return best;
    }
    public static void main(String[] args) { assert maxSubArray(new int[]{-2,1,-3,4,-1,2,1}) == 6; }
}
