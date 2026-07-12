// Practice: implement each method from scratch (String/char[]/control-flow only —
// no java.util.Collections here, that's module 03). Replace the
// "throw new UnsupportedOperationException" body with a real implementation.
// Run once you think you're done:  java -ea 01-basics/Solution.java
public class Solution {

    /** Case-insensitive palindrome check, ignoring anything that's not a letter/digit.
     *  isPalindrome("A man, a plan, a canal: Panama") -> true */
    static boolean isPalindrome(String s) {
        int r = s.length() - 1;
        int l = 0;
        while (l < r){
            if (s.charAt(l) != s.charAt(r)){
                return false;
            } 
            l++;
            r--;
        }
        return true;
    }

    /** Reverse the order of words, single-space separated, no leading/trailing space
     *  in the input to worry about.
     *  reverseWords("the sky is blue") -> "blue is sky the" */
    static String reverseWords(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = s.length() - 1; i>=0; i--){
            r.append(s.charAt(i));
        }
        return r.toString();
    }

    /** Sum of decimal digits of n. Handle negative n by summing the digits of |n|.
     *  sumDigits(-4931) -> 17 */
    static int sumDigits(int n) {
        int sum = 0;
        String n_s = String.valueOf(n);
        for(char e : n_s.toCharArray()){
            if(!Character.isDigit(e)){
                continue;
            }
            sum += e - '0';
        }
        return sum;
    }

    /** Run-length encode consecutive repeated characters.
     *  runLengthEncode("aaabbc") -> "a3b2c1"
     *  runLengthEncode("abc") -> "a1b1c1" */
    static String runLengthEncode(String s) {
        StringBuilder out = new StringBuilder();
        int cnt = 1;
        int n = s.length();
        for (int i = 0; i < n; i++){
            if (i == n-1){
                out.append(s.charAt(i));
                out.append(String.valueOf(cnt));
                break;
            }

            if (s.charAt(i) == s.charAt(i+1)){
                cnt++;
            } else
            {
                out.append(s.charAt(i));
                out.append(String.valueOf(cnt));
                cnt = 1;
            }

        }

        return out.toString();
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

        System.out.println("All good — module 01 Solution complete.");
    }
}
