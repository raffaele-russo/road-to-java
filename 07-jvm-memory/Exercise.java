// Practice: three small, deterministic experiments about JVM behavior. Run once you
// think you're done:  java -ea 07-jvm-memory/Exercise.java
public class Exercise {

    /** TODO: recurse (call yourself, accumulating a depth count) until a
     *  StackOverflowError is thrown; catch it and return the depth reached.
     *  Don't just loop — this needs to be genuine recursion to consume stack frames. */
    static int depthAtStackOverflow() {
        throw new UnsupportedOperationException("TODO");
    }

    static class Lazy {
        static int initCount = 0;
        static { initCount++; System.out.println("Lazy class initialized"); }
    }

    /** TODO: trigger Lazy's static initializer by actually instantiating it
     *  (referencing the class name alone, e.g. Lazy.class, would NOT trigger it). */
    static void touchLazy() {
        throw new UnsupportedOperationException("TODO");
    }

    record Point(int x, int y) {}

    /** TODO: return {a, b, c} where:
     *  a = true iff two equal-but-distinct Points have DIFFERENT System.identityHashCode()
     *  b = true iff those same two Points have EQUAL .hashCode() (value-based, from record)
     *  c = true iff calling System.identityHashCode() twice on the SAME reference is stable
     *      (returns the same value both times) */
    static boolean[] identityVsEquality() {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        int depth = depthAtStackOverflow();
        System.out.println("Reached recursion depth: " + depth);
        assert depth > 100 : "expected a deep recursion before overflow, got " + depth;

        // Note: we deliberately do NOT read Lazy.initCount before calling touchLazy() —
        // reading a non-constant static field is ITSELF a triggering event for class
        // initialization (JLS 12.4.1), so the mere act of checking "is it 0 yet?" would
        // trigger the very initialization we're trying to observe from the outside.
        touchLazy();
        assert Lazy.initCount == 1 : "static initializer should have run exactly once";
        touchLazy();
        assert Lazy.initCount == 1 : "static initializer must not re-run on subsequent use";

        boolean[] result = identityVsEquality();
        assert result[0] : "distinct objects should have different identity hash codes";
        assert result[1] : "records should have equal hashCode() for equal field values";
        assert result[2] : "identityHashCode of the same reference must be stable";

        System.out.println("All good — module 07 exercise complete.");
    }
}
