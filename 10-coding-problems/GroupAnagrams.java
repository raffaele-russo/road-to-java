import java.util.*;

/**
 * Group Anagrams: cluster words that are permutations of each other.
 * Pattern: computeIfAbsent bucketing keyed by the sorted-character signature.
 * O(n * k log k) where k = max word length.
 * Run:  java 10-coding-problems/GroupAnagrams.java
 */
public class GroupAnagrams {

    static List<List<String>> groupAnagrams(String[] words) {
        Map<String, List<String>> buckets = new HashMap<>();
        for (String w : words) {
            char[] c = w.toCharArray();
            Arrays.sort(c);
            String key = new String(c);                    // canonical signature
            buckets.computeIfAbsent(key, k -> new ArrayList<>()).add(w);
        }
        return new ArrayList<>(buckets.values());
    }

    public static void main(String[] args) {
        String[] input = {"eat", "tea", "tan", "ate", "nat", "bat"};
        List<List<String>> groups = groupAnagrams(input);
        // Sort each group + the outer list for stable printing.
        groups.forEach(Collections::sort);
        groups.sort(Comparator.comparing(g -> g.get(0)));
        System.out.println(groups);   // [[ate, eat, tea], [bat], [nat, tan]]
        assert groups.size() == 3 : "expected 3 groups";
        System.out.println("OK (run with -ea to enable assertions)");
    }
}
