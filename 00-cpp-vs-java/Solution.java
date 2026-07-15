// Reference solution for Exercise.java.
// Run with: java -ea 00-cpp-vs-java/Solution.java
public class Solution {

    // 1) Compare boxed values, not references. Module 01's autoboxing gotcha:
    //    the Integer cache only covers -128..127, so 1000 == 1000 (boxed) is false.
    static boolean compareValues(int a, int b) {
        Integer boxedA = a;
        Integer boxedB = b;
        return boxedA.equals(boxedB);
    }

    // 2) A C++ dev wants "pass by reference" for a primitive, the way int& works in C++.
    //    Java has no out-params for primitives — the idiomatic fix is to return the
    //    result instead of trying to mutate the parameter.
    static int doubleIt(int x) {
        return 2 * x;
    }

    // 3) A C++ dev builds a string in a loop the way they'd grow a std::string in place.
    //    This already returns the right VALUE — but it's O(n^2) (a new String object
    //    every iteration). StringBuilder keeps the loop linear.
    static String buildDashed(int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(i);
            if (i < n - 1) result.append("-");
        }
        return result.toString();
    }

    // 4) Interface fields are implicitly public static final.
    interface Config {
        int MAX_ATTEMPTS = 3;
    }

    // 5) AutoCloseable makes deterministic cleanup part of the Java type contract.
    static class Resource implements Config, AutoCloseable {
        final StringBuilder log;
        Resource(StringBuilder log) { this.log = log; log.append("open;"); }
        public void close() { log.append("close;"); }
    }

    public static void main(String[] args) {
        assert compareValues(1000, 1000) : "compareValues should compare values, not references";

        assert doubleIt(21) == 42 : "doubleIt should return the doubled value";

        assert buildDashed(5).equals("0-1-2-3-4") : "buildDashed produced: " + buildDashed(5);

        assert Config.MAX_ATTEMPTS == 3 : "interface constants are implicitly public static final";

        StringBuilder log = new StringBuilder();
        try (Resource r = new Resource(log)) {
            log.append("used;");
        }
        assert log.toString().equals("open;used;close;") : "got: " + log;

        System.out.println("All good — every C++ habit ported correctly.");
    }
}
