import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Practice (unsolved): given a list of intervals, merge all overlapping ones.
// Pattern: sort by start, then walk once, extending or starting a new interval — O(n log n).
// Run once you think you're done:  java -ea 10-coding-problems/MergeIntervals.java
public class MergeIntervals {

    /** intervals like {1,3},{2,6},{8,10},{15,18} -> {1,6},{8,10},{15,18} */
    static int[][] merge(int[][] intervals) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        int[][] result = merge(new int[][]{{1, 3}, {2, 6}, {8, 10}, {15, 18}});
        assert Arrays.deepEquals(result, new int[][]{{1, 6}, {8, 10}, {15, 18}})
            : "got: " + Arrays.deepToString(result);

        int[][] touching = merge(new int[][]{{1, 4}, {4, 5}});
        assert Arrays.deepEquals(touching, new int[][]{{1, 5}}) : "got: " + Arrays.deepToString(touching);

        int[][] none = merge(new int[][]{{1, 2}, {3, 4}});
        assert Arrays.deepEquals(none, new int[][]{{1, 2}, {3, 4}}) : "got: " + Arrays.deepToString(none);

        int[][] unsorted = merge(new int[][]{{5, 6}, {1, 2}, {2, 4}});
        assert Arrays.deepEquals(unsorted, new int[][]{{1, 4}, {5, 6}}) : "got: " + Arrays.deepToString(unsorted);

        System.out.println("All good — MergeIntervals complete.");
    }
}
