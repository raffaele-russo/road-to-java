import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * Valid Parentheses: check that brackets are balanced and correctly nested.
 * Pattern: stack (ArrayDeque) — push openers, pop-and-match on closers.
 * O(n) time, O(n) space.
 * Run:  java 10-coding-problems/ValidParentheses.java
 */
public class ValidParentheses {

    private static final Map<Character, Character> PAIRS = Map.of(')', '(', ']', '[', '}', '{');

    static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : s.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (PAIRS.containsKey(c)) {
                if (stack.isEmpty() || stack.pop() != PAIRS.get(c)) return false;
            }
        }
        return stack.isEmpty();          // nothing left unmatched
    }

    public static void main(String[] args) {
        String[] valid = {"()", "()[]{}", "{[()]}", ""};
        String[] invalid = {"(]", "([)]", "(((", "}"};

        for (String s : valid) {
            System.out.println("isValid(\"" + s + "\") = " + isValid(s));
            assert isValid(s) : s + " should be valid";
        }
        for (String s : invalid) {
            System.out.println("isValid(\"" + s + "\") = " + isValid(s));
            assert !isValid(s) : s + " should be invalid";
        }
        System.out.println("OK (run with -ea to enable assertions)");
    }
}
