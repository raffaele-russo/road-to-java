// Practice: each method below is written the way a C++ developer's first instinct
// would write it in Java. Each one is broken or non-idiomatic in a way this module
// explains. Fix the TODOs so every assert in main() passes.
//
// NOTE: this file does NOT compile yet. Exercise 5 (Resource) is a compile-time error
// on purpose — try-with-resources requires AutoCloseable at compile time, unlike C++
// where "forgetting RAII" just silently compiles and leaks. Fix #5 first to get a clean
// compile, then chase the runtime assert failures for #1-#4.
//
// Run once you think you're done:  java -ea 00-cpp-vs-java/Exercise.java
public class Exercise {

    // 1) A C++ dev reaches for == to compare "value" objects out of habit.
    //    TODO: fix compareValues() so it compares the two Integer VALUES, not references.
    //    Do not change the signature. Hint: module 01's autoboxing gotcha —
    //    the Integer cache only covers -128..127, so 1000 == 1000 (boxed) is false.
    static boolean compareValues(int a, int b) {
        Integer boxedA = a;
        Integer boxedB = b;
        return boxedA == boxedB; // BUG: reference comparison
    }

    // 2) A C++ dev wants "pass by reference" for a primitive, the way int& works in C++.
    //    Java has no out-params for primitives — the idiomatic fix is to return the
    //    result instead of trying to mutate the parameter. This already returns an int,
    //    but the body forgot to actually double it. TODO: fix the body.
    static int doubleIt(int x) {
        return x; // BUG: doesn't double anything
    }

    // 3) A C++ dev builds a string in a loop the way they'd grow a std::string in place.
    //    This already returns the right VALUE — but it's O(n^2) (a new String object
    //    every iteration). TODO: rewrite the body using StringBuilder (module 01) so
    //    it's O(n) while still returning the same value.
    static String buildDashed(int n) {
        String result = "";
        for (int i = 0; i < n; i++) {
            result += i;
            if (i < n - 1) result += "-";
        }
        return result;
    }

    // 4) A C++ dev assumes an interface field is a regular field, defaultable to 0.
    //    TODO: interface fields are implicitly `public static final` — fix the value
    //    itself (not the type/modifiers, those are already correct) to 3.
    interface Config {
        int MAX_ATTEMPTS = 0; // TODO: wrong value
    }

    // 5) A C++ dev wants deterministic cleanup the way a destructor would give them.
    //    TODO: make Resource implement the right interface (module 00/04) so it can be
    //    used in a try-with-resources block and close() runs automatically.
    static class Resource /* TODO: implements ??? */ {
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
