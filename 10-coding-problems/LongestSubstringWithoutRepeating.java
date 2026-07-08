import java.util.HashMap;
import java.util.Map;

/**
 * Longest Substring Without Repeating Characters.
 * Pattern: sliding window with a HashMap of last-seen index — expand the right
 * edge, jump the left edge past any repeat. O(n) time, O(min(n, alphabet)) space.
 * Run:  java 10-coding-problems/LongestSubstringWithoutRepeating.java
 */
public class LongestSubstringWithoutRepeating {

    static int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> lastSeen = new HashMap<>();
        int longest = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (lastSeen.containsKey(c) && lastSeen.get(c) >= left) {
                left = lastSeen.get(c) + 1;              // jump window start past the repeat
            }
            lastSeen.put(c, right);
            longest = Math.max(longest, right - left + 1);
        }
        return longest;
    }

    public static void main(String[] args) {
        System.out.println("\"abcabcbb\" -> " + lengthOfLongestSubstring("abcabcbb")); // 3 (abc)
        assert lengthOfLongestSubstring("abcabcbb") == 3;

        System.out.println("\"bbbbb\"    -> " + lengthOfLongestSubstring("bbbbb"));    // 1 (b)
        assert lengthOfLongestSubstring("bbbbb") == 1;

        System.out.println("\"pwwkew\"   -> " + lengthOfLongestSubstring("pwwkew"));   // 3 (wke)
        assert lengthOfLongestSubstring("pwwkew") == 3;

        System.out.println("\"\"         -> " + lengthOfLongestSubstring(""));         // 0
        assert lengthOfLongestSubstring("") == 0;

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
