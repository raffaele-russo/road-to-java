import java.util.ArrayList;
import java.util.List;

/**
 * Generic classes/methods, bounded type params, wildcards (PECS), and the
 * type-erasure limitations to name in an interview.
 * Run:  java 03-collections-generics/Generics.java
 */
public class Generics {

    public static void main(String[] args) {
        genericClass();
        boundedMethod();
        pecsWildcards();
        erasureNotes();
    }

    // ---- generic class ----
    static class Box<T> {
        private final T value;
        Box(T value) { this.value = value; }
        T get() { return value; }
        <R> Box<R> map(java.util.function.Function<T, R> f) { return new Box<>(f.apply(value)); }
    }

    static void genericClass() {
        System.out.println("== generic class ==");
        Box<String> b = new Box<>("hello");
        Box<Integer> len = b.map(String::length);
        System.out.println("  Box<String> -> Box<Integer>: " + len.get());
    }

    // ---- bounded type parameter ----
    static <T extends Comparable<T>> T max(List<T> items) {
        T best = items.get(0);
        for (T x : items) if (x.compareTo(best) > 0) best = x;
        return best;
    }

    static void boundedMethod() {
        System.out.println("\n== bounded type param <T extends Comparable<T>> ==");
        System.out.println("  max ints : " + max(List.of(3, 9, 2, 7)));
        System.out.println("  max words: " + max(List.of("pear", "apple", "zebra")));
    }

    // ---- PECS: Producer Extends, Consumer Super ----
    static double sum(List<? extends Number> producers) {   // read out
        double total = 0;
        for (Number n : producers) total += n.doubleValue();
        return total;
    }
    static void addInts(List<? super Integer> consumer) {    // write in
        consumer.add(1); consumer.add(2);
    }

    static void pecsWildcards() {
        System.out.println("\n== wildcards (PECS) ==");
        System.out.println("  sum(List<Double>): " + sum(List.of(1.5, 2.5)));
        System.out.println("  sum(List<Integer>): " + sum(List.of(1, 2, 3)));
        List<Number> sink = new ArrayList<>();
        addInts(sink);                          // Integer fits into List<? super Integer>
        System.out.println("  after addInts: " + sink);
    }

    static void erasureNotes() {
        System.out.println("\n== type erasure (runtime facts) ==");
        List<String> a = new ArrayList<>();
        List<Integer> b = new ArrayList<>();
        // Both are just `ArrayList` at runtime:
        System.out.println("  same runtime class: " + (a.getClass() == b.getClass())); // true
        // Illegal (won't compile), for reference:
        //   if (a instanceof List<String>) ...   // reifiable-only: use List<?>
        //   T t = new T();                        // cannot instantiate type param
        //   T[] arr = new T[10];                  // cannot create generic array
        System.out.println("  -> no new T(), no new T[], no List<int>, no reified generics");
    }
}
