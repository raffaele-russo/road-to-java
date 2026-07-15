import java.util.List;
import java.util.Objects;

// Reference solution for Exercise.java. Run with: java -ea 02-oop/Solution.java
public class Solution {

    interface Describable {
        String describe();                                  // abstract
        default String shout() { return describe().toUpperCase() + "!"; }  // default method, free
    }

    abstract static class Shape {
        abstract double area();

        /** Sum by polymorphic dispatch; no type switch is needed. */
        static double totalArea(List<Shape> shapes) {
            double sum = 0;
            for (Shape s : shapes) {
                sum += s.area();
            }
            return sum;
        }
    }

    /** Immutable value object whose equality is based on radius. */
    static class Circle extends Shape implements Describable {
        private final double radius;

        Circle(double radius) { this.radius = radius; }

        @Override
        double area() { return radius * radius * Math.PI; }

        @Override
        public String describe() { return "Circle(r=" + radius + ")"; }

        @Override
        public int hashCode() {
            return Objects.hash(radius);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            return obj instanceof Circle c && Double.compare(c.radius, radius) == 0;
        }

        @Override
        public String toString() {
            return describe();
        }
    
    }

    /** Immutable value object whose equality is based on both dimensions. */
    static class Rectangle extends Shape implements Describable {
        private final double width;
        private final double height;

        Rectangle(double width, double height) { this.width = width; this.height = height; }

        double area() { return width * height; }
        @Override
        public String describe(){
            return "Rectangle(w=" + width + ", h=" + height + ")";
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            return obj instanceof Rectangle r
                    && Double.compare(r.width, width) == 0
                    && Double.compare(r.height, height) == 0;
        }

        @Override
        public String toString() {
            return describe();
        }
    }

    public static void main(String[] args) {
        Circle c1 = new Circle(2.0);
        Circle c2 = new Circle(2.0);
        Circle c3 = new Circle(3.0);
        Rectangle r1 = new Rectangle(2.0, 5.0);

        assert c1.equals(c2) : "circles with the same radius must be equal";
        assert c1.hashCode() == c2.hashCode() : "equal objects must have equal hashCodes";
        assert !c1.equals(c3) : "circles with different radii must not be equal";
        assert !c1.equals(r1) : "a Circle must never equal a Rectangle";

        assert Math.abs(c1.area() - Math.PI * 4) < 1e-9;
        assert Math.abs(r1.area() - 10.0) < 1e-9;

        assert c1.describe().equals("Circle(r=2.0)") : "got: " + c1.describe();
        assert r1.describe().equals("Rectangle(w=2.0, h=5.0)") : "got: " + r1.describe();
        assert c1.shout().equals("CIRCLE(R=2.0)!") : "default method should reuse describe()";

        assert c1.toString().equals(c1.describe());

        double total = Shape.totalArea(List.of(c1, r1));
        assert Math.abs(total - (Math.PI * 4 + 10.0)) < 1e-9 : "got: " + total;

        System.out.println("All good — module 02 Solution complete.");
    }
}
