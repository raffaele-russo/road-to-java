import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Strategy: swap an algorithm at runtime. Shows the classic OOP form (an
 * interface + implementing classes) and the modern Java 8+ form (a functional
 * interface + lambdas) — a common interview follow-up is "how did lambdas
 * change this pattern?"
 * Run:  java 11-design-patterns/Strategy.java
 */
public class Strategy {

    // ---- classic OOP strategy ----
    interface DiscountStrategy {
        double apply(double price);
    }
    static class NoDiscount implements DiscountStrategy {
        public double apply(double price) { return price; }
    }
    static class PercentOffDiscount implements DiscountStrategy {
        private final double percent;
        PercentOffDiscount(double percent) { this.percent = percent; }
        public double apply(double price) { return price * (1 - percent / 100); }
    }
    static class FlatOffDiscount implements DiscountStrategy {
        private final double amount;
        FlatOffDiscount(double amount) { this.amount = amount; }
        public double apply(double price) { return Math.max(0, price - amount); }
    }

    static double checkout(double price, DiscountStrategy strategy) {
        return strategy.apply(price);          // algorithm swapped at runtime
    }

    public static void main(String[] args) {
        System.out.println("== classic OOP strategy ==");
        System.out.printf("  no discount    : $%.2f%n", checkout(100, new NoDiscount()));
        System.out.printf("  20%% off        : $%.2f%n", checkout(100, new PercentOffDiscount(20)));
        System.out.printf("  $15 flat off   : $%.2f%n", checkout(100, new FlatOffDiscount(15)));

        System.out.println("\n== modern strategy: a lambda IS the strategy (no class needed) ==");
        System.out.printf("  30%% off (lambda): $%.2f%n", checkout(100, price -> price * 0.7));

        System.out.println("\n== another everyday example: Comparator as strategy ==");
        List<String> names = new java.util.ArrayList<>(List.of("charlie", "al", "bo"));
        sortWith(names, Comparator.comparingInt(String::length));   // strategy: by length
        System.out.println("  by length: " + names);
        sortWith(names, Comparator.reverseOrder());                 // strategy: reverse alpha
        System.out.println("  reverse  : " + names);

        System.out.println("\n== BinaryOperator as a reduce strategy ==");
        BinaryOperator<Integer> sum = Integer::sum;
        BinaryOperator<Integer> max = Integer::max;
        System.out.println("  reduce with sum: " + List.of(1, 2, 3).stream().reduce(0, sum));
        System.out.println("  reduce with max: " + List.of(1, 5, 3).stream().reduce(0, max));
    }

    static void sortWith(List<String> list, Comparator<String> strategy) { list.sort(strategy); }
}
