// Practice: implement each method from scratch (String/char[]/control-flow only —
// no java.util.Collections here, that's module 03). Replace the
// "throw new UnsupportedOperationException" body with a real implementation.
// Run once you think you're done:  java -ea 01-basics/Exercise.java
public class Exercise {

    /** Case-insensitive palindrome check, ignoring anything that's not a letter/digit.
     *  isPalindrome("A man, a plan, a canal: Panama") -> true */
    static boolean isPalindrome(String s) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Reverse the order of words, single-space separated, no leading/trailing space
     *  in the input to worry about.
     *  reverseWords("the sky is blue") -> "blue is sky the" */
    static String reverseWords(String s) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Sum of decimal digits of n. Handle negative n by summing the digits of |n|.
     *  sumDigits(-4931) -> 17 */
    static int sumDigits(int n) {
        throw new UnsupportedOperationException("TODO");
    }

    /** Run-length encode consecutive repeated characters.
     *  runLengthEncode("aaabbc") -> "a3b2c1"
     *  runLengthEncode("abc") -> "a1b1c1" */
    static String runLengthEncode(String s) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        assert isPalindrome("A man, a plan, a canal: Panama");
        assert !isPalindrome("not a palindrome");
        assert isPalindrome("");

        assert reverseWords("the sky is blue").equals("blue is sky the");
        assert reverseWords("hello").equals("hello");

        assert sumDigits(-4931) == 17;
        assert sumDigits(0) == 0;

        assert runLengthEncode("aaabbc").equals("a3b2c1");
        assert runLengthEncode("abc").equals("a1b1c1");

        System.out.println("All good — module 01 exercise complete.");
    }
}
