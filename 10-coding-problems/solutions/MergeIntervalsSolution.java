import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MergeIntervalsSolution {
    static int[][] merge(int[][] intervals) {
        if (intervals.length == 0) return new int[0][];
        int[][] sorted = Arrays.stream(intervals).map(int[]::clone).toArray(int[][]::new);
        Arrays.sort(sorted, java.util.Comparator.comparingInt(a -> a[0]));
        List<int[]> merged = new ArrayList<>();
        for (int[] interval : sorted) {
            if (merged.isEmpty() || merged.getLast()[1] < interval[0]) merged.add(interval);
            else merged.getLast()[1] = Math.max(merged.getLast()[1], interval[1]);
        }
        return merged.toArray(int[][]::new);
    }
    public static void main(String[] args) {
        assert Arrays.deepEquals(merge(new int[][]{{2,6},{1,3}}), new int[][]{{1,6}});
    }
}
