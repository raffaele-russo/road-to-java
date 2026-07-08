import java.util.ArrayList;
import java.util.List;

/**
 * Permutations of a list: the canonical backtracking template — choose, recurse,
 * un-choose. O(n! * n) time.
 * Run:  java 10-coding-problems/Permutations.java
 */
public class Permutations {

    static List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
        return result;
    }

    static void backtrack(int[] nums, List<Integer> current, boolean[] used, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));   // snapshot — current keeps mutating
            return;
        }
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            used[i] = true;
            current.add(nums[i]);                    // choose
            backtrack(nums, current, used, result);   // recurse
            current.remove(current.size() - 1);       // un-choose (backtrack)
            used[i] = false;
        }
    }

    public static void main(String[] args) {
        List<List<Integer>> perms = permute(new int[]{1, 2, 3});
        perms.forEach(System.out::println);
        assert perms.size() == 6 : "3! = 6 permutations expected";
        assert perms.contains(List.of(1, 2, 3));
        assert perms.contains(List.of(3, 2, 1));
        System.out.println("total: " + perms.size());
        System.out.println("OK (run with -ea to enable assertions)");
    }
}
