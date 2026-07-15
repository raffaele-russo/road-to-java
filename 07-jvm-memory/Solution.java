/** Reference solution for Exercise.java. */
public class Solution {
    private static int recurse(int depth) {
        try { return recurse(depth + 1); }
        catch (StackOverflowError error) { return depth; }
    }

    static int depthAtStackOverflow() { return recurse(1); }

    static class Lazy {
        static int initCount;
        static { initCount++; }
    }

    static void touchLazy() { new Lazy(); }

    record Point(int x, int y) {}

    static boolean[] identityVsEquality() {
        Point first = new Point(1, 2);
        Point second = new Point(1, 2);
        int identity = System.identityHashCode(first);
        return new boolean[] {
            first != second,
            first.hashCode() == second.hashCode(),
            identity == System.identityHashCode(first)
        };
    }

    public static void main(String[] args) {
        assert depthAtStackOverflow() > 100;
        touchLazy(); touchLazy();
        assert Lazy.initCount == 1;
        System.out.println("Module 07 reference solution passed.");
    }
}
