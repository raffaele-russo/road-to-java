import java.util.Arrays;

/**
 * Merge sort and quicksort implemented from scratch — interviewers ask these
 * to check you understand divide-and-conquer and partitioning, not just
 * Arrays.sort(). Both O(n log n) average; quicksort O(n^2) worst case.
 * Run:  java 10-coding-problems/Sorting.java
 */
public class Sorting {

    // ---- merge sort: O(n log n) guaranteed, O(n) extra space, stable ----
    static void mergeSort(int[] a) { mergeSort(a, 0, a.length - 1); }

    static void mergeSort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int mid = lo + (hi - lo) / 2;
        mergeSort(a, lo, mid);
        mergeSort(a, mid + 1, hi);
        merge(a, lo, mid, hi);
    }

    static void merge(int[] a, int lo, int mid, int hi) {
        int[] tmp = new int[hi - lo + 1];
        int i = lo, j = mid + 1, k = 0;
        while (i <= mid && j <= hi) tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
        while (i <= mid) tmp[k++] = a[i++];
        while (j <= hi) tmp[k++] = a[j++];
        System.arraycopy(tmp, 0, a, lo, tmp.length);
    }

    // ---- quicksort: O(n log n) average, in-place, NOT stable ----
    static void quickSort(int[] a) { quickSort(a, 0, a.length - 1); }

    static void quickSort(int[] a, int lo, int hi) {
        if (lo >= hi) return;
        int p = partition(a, lo, hi);
        quickSort(a, lo, p - 1);
        quickSort(a, p + 1, hi);
    }

    static int partition(int[] a, int lo, int hi) {
        int pivot = a[hi];             // Lomuto partition scheme, pivot = last element
        int i = lo - 1;
        for (int j = lo; j < hi; j++) {
            if (a[j] < pivot) { i++; swap(a, i, j); }
        }
        swap(a, i + 1, hi);
        return i + 1;
    }

    static void swap(int[] a, int i, int j) { int t = a[i]; a[i] = a[j]; a[j] = t; }

    public static void main(String[] args) {
        int[] a = {5, 3, 8, 4, 2, 7, 1, 10};
        int[] b = a.clone();

        mergeSort(a);
        System.out.println("merge sort : " + Arrays.toString(a));
        assert isSorted(a);

        quickSort(b);
        System.out.println("quicksort  : " + Arrays.toString(b));
        assert isSorted(b);

        System.out.println("OK (run with -ea to enable assertions)");
    }

    static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i - 1] > a[i]) return false;
        return true;
    }
}
