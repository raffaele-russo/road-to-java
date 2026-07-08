/**
 * Decorator: attach behavior to an object dynamically by wrapping it in another
 * object implementing the same interface. This is exactly how java.io works:
 * new BufferedReader(new InputStreamReader(System.in)) is decorators stacked.
 * Run:  java 11-design-patterns/Decorator.java
 */
public class Decorator {

    interface Coffee {
        double cost();
        String description();
    }

    static class Espresso implements Coffee {
        public double cost() { return 2.0; }
        public String description() { return "Espresso"; }
    }

    // Base decorator: implements the same interface, wraps another Coffee.
    static abstract class CoffeeDecorator implements Coffee {
        protected final Coffee wrapped;
        CoffeeDecorator(Coffee wrapped) { this.wrapped = wrapped; }
    }

    static class WithMilk extends CoffeeDecorator {
        WithMilk(Coffee c) { super(c); }
        public double cost() { return wrapped.cost() + 0.5; }
        public String description() { return wrapped.description() + " + milk"; }
    }

    static class WithCaramel extends CoffeeDecorator {
        WithCaramel(Coffee c) { super(c); }
        public double cost() { return wrapped.cost() + 0.7; }
        public String description() { return wrapped.description() + " + caramel"; }
    }

    public static void main(String[] args) {
        System.out.println("== decorator ==");
        Coffee order = new WithCaramel(new WithMilk(new Espresso())); // stack decorators
        System.out.printf("  %s -> $%.2f%n", order.description(), order.cost());
        // No subclass explosion needed for EspressoWithMilkAndCaramel etc.

        System.out.println("\n  real Java example (java.io does exactly this):");
        System.out.println("    new BufferedReader(new InputStreamReader(System.in))");
        System.out.println("    each layer implements Reader and wraps another Reader");
    }
}
