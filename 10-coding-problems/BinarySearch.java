/**
 * Binary search: the classic form plus the "find first/last occurrence" variant
 * that trips people up (off-by-one errors in the boundary update).
 * O(log n) time, O(1) space.
 * Run:  java 10-coding-problems/BinarySearch.java
 */
public class BinarySearch {

    static int search(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;   // avoids (lo+hi) overflow — say this in interviews
            if (a[mid] == target) return mid;
            if (a[mid] < target) lo = mid + 1;
            else hi = mid - 1;
        }
        return -1;
    }

    // Find the leftmost index whose value >= target (a.k.a. lower_bound).
    static int lowerBound(int[] a, int target) {
        int lo = 0, hi = a.length;           // note: hi = length, not length - 1
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (a[mid] < target) lo = mid + 1;
            else hi = mid;
        }
        return lo;
    }

    // Find first and last index of target in a sorted array with duplicates.
    static int[] searchRange(int[] a, int target) {
        int first = lowerBound(a, target);
        if (first == a.length || a[first] != target) return new int[]{-1, -1};
        int last = lowerBound(a, target + 1) - 1;
        return new int[]{first, last};
    }

    public static void main(String[] args) {
        int[] a = {1, 3, 5, 7, 9, 11};
        System.out.println("search(7)  = " + search(a, 7));
        System.out.println("search(4)  = " + search(a, 4));
        assert search(a, 7) == 3;
        assert search(a, 4) == -1;

        int[] dup = {5, 7, 7, 7, 8, 9};
        int[] range = searchRange(dup, 7);
        System.out.println("searchRange(7) = [" + range[0] + ", " + range[1] + "]");
        assert range[0] == 1 && range[1] == 3;

        int[] noMatch = searchRange(dup, 6);
        System.out.println("searchRange(6) = [" + noMatch[0] + ", " + noMatch[1] + "]");
        assert noMatch[0] == -1 && noMatch[1] == -1;

        System.out.println("OK (run with -ea to enable assertions)");
    }
}
