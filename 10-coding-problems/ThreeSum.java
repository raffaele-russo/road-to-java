import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 3Sum: find all unique triplets summing to zero.
 * Pattern: sort, then fix one element and two-pointer walk the rest inward.
 * O(n^2) time (vs O(n^3) brute force), O(1) extra space (excluding output).
 * Run:  java 10-coding-problems/ThreeSum.java
 */
public class ThreeSum {

    static List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);                             // enables two-pointer + easy dedup
        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1]) continue;  // skip duplicate anchors
            int lo = i + 1, hi = nums.length - 1;
            while (lo < hi) {
                int sum = nums[i] + nums[lo] + nums[hi];
                if (sum == 0) {
                    result.add(List.of(nums[i], nums[lo], nums[hi]));
                    while (lo < hi && nums[lo] == nums[lo + 1]) lo++;  // skip duplicates
                    while (lo < hi && nums[hi] == nums[hi - 1]) hi--;
                    lo++; hi--;
                } else if (sum < 0) {
                    lo++;                                // need a bigger sum
                } else {
                    hi--;                                // need a smaller sum
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        List<List<Integer>> result = threeSum(new int[]{-1, 0, 1, 2, -1, -4});
        System.out.println(result);
        assert result.size() == 2;
        assert result.contains(List.of(-1, -1, 2));
        assert result.contains(List.of(-1, 0, 1));
        System.out.println("OK (run with -ea to enable assertions)");
    }
}
