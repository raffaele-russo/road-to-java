/**
 * Demonstrates the core semantic differences a C++ dev must internalize.
 * Run:  java 00-cpp-vs-java/HelloComparison.java
 */
public class HelloComparison {

    public static void main(String[] args) {
        referenceSemantics();
        passByValueOfReference();
        finalIsNotConst();
        equalsVsDoubleEquals();
        integerOverflowIsDefined();
    }

    // Objects are references. Assignment shares, it does not copy.
    static void referenceSemantics() {
        System.out.println("== reference semantics ==");
        StringBuilder a = new StringBuilder("hi");
        StringBuilder b = a;          // same object, not a copy
        b.append("!");
        System.out.println("a = " + a + "  (mutated through b)"); // hi!
        System.out.println("a == b ? " + (a == b));               // true, same ref
    }

    // Java is pass-by-value; for objects the value is the reference.
    static void passByValueOfReference() {
        System.out.println("\n== pass-by-value-of-reference ==");
        int[] arr = {1, 2, 3};
        mutate(arr);
        System.out.println("arr[0] after mutate = " + arr[0]); // 99 (object mutated)
        System.out.println("arr still length 3 = " + (arr.length == 3)); // reassign didn't leak
    }

    static void mutate(int[] arr) {
        arr[0] = 99;              // visible to caller
        arr = new int[]{7, 8};    // NOT visible — only our local copy of the ref changed
    }

    // final blocks reassignment, not mutation. There is no const-correctness.
    static void finalIsNotConst() {
        System.out.println("\n== final != const ==");
        final int[] xs = {1, 2, 3};
        xs[0] = 42;                    // allowed — object is mutable
        // xs = new int[]{9};          // would NOT compile
        System.out.println("xs[0] = " + xs[0]);
    }

    // == compares references for objects; .equals() compares value.
    static void equalsVsDoubleEquals() {
        System.out.println("\n== == vs equals ==");
        String x = new String("java");
        String y = new String("java");
        System.out.println("x == y      : " + (x == y));      // false (different objects)
        System.out.println("x.equals(y) : " + x.equals(y));   // true  (same value)

        // Autoboxing gotcha: small Integers are cached (-128..127), large ones are not.
        Integer p = 127, q = 127;
        Integer r = 1000, s = 1000;
        System.out.println("127 == 127   : " + (p == q)); // true  (cached)
        System.out.println("1000 == 1000 : " + (r == s)); // false (distinct objects!)
    }

    // Unlike C++ signed overflow (UB), Java defines two's-complement wraparound.
    static void integerOverflowIsDefined() {
        System.out.println("\n== integer overflow is defined ==");
        int max = Integer.MAX_VALUE;
        System.out.println("MAX_VALUE + 1 = " + (max + 1)); // wraps to Integer.MIN_VALUE
    }
}
