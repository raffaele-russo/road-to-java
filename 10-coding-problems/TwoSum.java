import java.util.HashMap;
import java.util.Map;

/**
 * Two Sum: return indices of the two numbers adding up to target.
 * Pattern: one-pass HashMap of value -> index. O(n) time, O(n) space.
 * Run:  java 10-coding-problems/TwoSum.java
 */
public class TwoSum {

    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();      // value -> index
        for (int i = 0; i < nums.length; i++) {
            int need = target - nums[i];
            if (seen.containsKey(need)) return new int[]{seen.get(need), i};
            seen.put(nums[i], i);
        }
        return new int[]{-1, -1};
    }

    public static void main(String[] args) {
        int[] r = twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println("indices: [" + r[0] + ", " + r[1] + "]");   // [0, 1]
        assert r[0] == 0 && r[1] == 1 : "expected [0,1]";

        int[] r2 = twoSum(new int[]{3, 2, 4}, 6);
        System.out.println("indices: [" + r2[0] + ", " + r2[1] + "]"); // [1, 2]
        assert r2[0] == 1 && r2[1] == 2;

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
