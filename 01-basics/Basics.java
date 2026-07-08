import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Primitives, wrappers, strings, arrays, control flow.
 * Run:  java 01-basics/Basics.java
 */
public class Basics {

    public static void main(String[] args) {
        primitivesAndOverflow();
        autoboxing();
        strings();
        arrays();
        controlFlow();
        varInference();
    }

    static void primitivesAndOverflow() {
        System.out.println("== primitives ==");
        int i = 42;
        long big = 10_000_000_000L;   // needs L; underscores allowed in literals
        double d = 3.14;
        char c = 'A';                 // 16-bit UTF-16 unit
        boolean flag = true;
        System.out.printf("int=%d long=%d double=%.2f char=%c bool=%b%n", i, big, d, c, flag);
        System.out.println("char 'A' as int = " + (int) c); // 65
        System.out.println("byte range: " + Byte.MIN_VALUE + ".." + Byte.MAX_VALUE);
    }

    static void autoboxing() {
        System.out.println("\n== autoboxing ==");
        List<Integer> nums = new ArrayList<>();
        nums.add(5);                 // int -> Integer
        int x = nums.get(0);         // Integer -> int
        System.out.println("boxed then unboxed: " + x);

        Integer maybeNull = null;
        try {
            int boom = maybeNull;    // unboxing null -> NPE
            System.out.println(boom);
        } catch (NullPointerException e) {
            System.out.println("unboxing null threw NPE (as expected)");
        }
    }

    static void strings() {
        System.out.println("\n== strings ==");
        String a = "Ja", b = "va";
        String s = a + b;                       // + is overloaded for String
        System.out.println("concat: " + s);
        System.out.println("length: " + s.length());
        System.out.println("upper : " + s.toUpperCase());
        System.out.println("chars : " + Arrays.toString(s.chars().toArray())); // code points

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) sb.append(i).append(',');
        System.out.println("built : " + sb);

        // Text block (Java 15+)
        String json = """
            {
              "lang": "java",
              "year": 2026
            }""";
        System.out.println("text block:\n" + json);
    }

    static void arrays() {
        System.out.println("\n== arrays ==");
        int[] nums = {5, 3, 8, 1};
        System.out.println("length field: " + nums.length);
        Arrays.sort(nums);
        System.out.println("sorted: " + Arrays.toString(nums));

        int[][] grid = new int[2][3];  // arrays of arrays
        grid[1][2] = 9;
        System.out.println("grid: " + Arrays.deepToString(grid));

        try {
            int bad = nums[99];        // bounds-checked, no UB
            System.out.println(bad);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("out of bounds throws (no UB): " + e.getMessage());
        }
    }

    static void controlFlow() {
        System.out.println("\n== control flow ==");
        List<String> words = List.of("alpha", "beta", "gamma");
        for (String w : words) System.out.print(w + " ");     // for-each
        System.out.println();

        // labeled break
        outer:
        for (int r = 0; r < 3; r++)
            for (int col = 0; col < 3; col++)
                if (r + col == 3) { System.out.println("break at " + r + "," + col); break outer; }
    }

    static void varInference() {
        System.out.println("\n== var ==");
        var list = new ArrayList<String>(); // inferred ArrayList<String>
        list.add("inferred");
        var n = 10;                          // int
        System.out.println(list + " n=" + n);
    }
}
