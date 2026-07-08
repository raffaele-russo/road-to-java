import java.util.List;
import java.util.Objects;

/**
 * Classes, inheritance (virtual by default), interfaces with default methods,
 * equals/hashCode, and enums-as-classes.
 * Run:  java 02-oop/Oop.java
 */
public class Oop {

    public static void main(String[] args) {
        polymorphism();
        interfacesAndDefaults();
        equalsAndHashCode();
        enums();
    }

    // ---- inheritance / virtual dispatch ----
    static abstract class Shape {
        abstract double area();                 // no body
        String describe() { return getClass().getSimpleName() + " area=" + area(); }
    }
    static class Circle extends Shape {
        final double r;
        Circle(double r) { this.r = r; }
        @Override double area() { return Math.PI * r * r; }
    }
    static class Square extends Shape {
        final double side;
        Square(double side) { this.side = side; }
        @Override double area() { return side * side; }
    }

    static void polymorphism() {
        System.out.println("== polymorphism (methods virtual by default) ==");
        List<Shape> shapes = List.of(new Circle(2), new Square(3));
        for (Shape s : shapes) System.out.printf("  %s%n", s.describe());
    }

    // ---- interfaces with default methods (Java's multiple inheritance) ----
    interface Named { String name(); }
    interface Greeter extends Named {
        default String greet() { return "Hello, " + name() + "!"; } // default method body
    }
    static class Person implements Greeter {
        private final String name;
        Person(String name) { this.name = name; }
        @Override public String name() { return name; }
    }

    static void interfacesAndDefaults() {
        System.out.println("\n== interfaces + default methods ==");
        Greeter g = new Person("Ada");
        System.out.println("  " + g.greet());
    }

    // ---- equals / hashCode contract ----
    static final class Point {
        final int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point p)) return false;  // pattern-matching instanceof
            return x == p.x && y == p.y;
        }
        @Override public int hashCode() { return Objects.hash(x, y); }
        @Override public String toString() { return "(" + x + "," + y + ")"; }
    }

    static void equalsAndHashCode() {
        System.out.println("\n== equals / hashCode ==");
        Point a = new Point(1, 2), b = new Point(1, 2);
        System.out.println("  a == b        : " + (a == b));       // false (different objects)
        System.out.println("  a.equals(b)   : " + a.equals(b));    // true  (value equality)
        System.out.println("  same hashCode : " + (a.hashCode() == b.hashCode()));
        var set = new java.util.HashSet<Point>();
        set.add(a); set.add(b);
        System.out.println("  set size (dedup by value): " + set.size()); // 1
    }

    // ---- enums as full classes ----
    enum Planet {
        EARTH(9.81), MARS(3.71);
        private final double gravity;
        Planet(double g) { this.gravity = g; }
        double weight(double mass) { return mass * gravity; }
    }

    static void enums() {
        System.out.println("\n== enums with state + behavior ==");
        for (Planet p : Planet.values())
            System.out.printf("  75kg on %-6s = %.1f N%n", p, p.weight(75));
    }
}
