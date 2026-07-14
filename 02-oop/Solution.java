import java.util.List;
import java.util.Objects;

// Practice: implement Circle and Rectangle below (marked TODO). Shape and Describable
// are already complete — don't change them. Run once you think you're done:
//   java -ea 02-oop/Solution.java
public class Solution {

    interface Describable {
        String describe();                                  // abstract
        default String shout() { return describe().toUpperCase() + "!"; }  // default method, free
    }

    abstract static class Shape {
        abstract double area();

        /** TODO: sum the area() of every shape in the list. Use a plain loop or a stream —
         *  either is fine, this file doesn't require streams (module 05). */
        static double totalArea(List<Shape> shapes) {
            double sum = 0;
            for (Shape s : shapes){
                sum += s.area();
            }
            return sum;
        }
    }

    /** TODO: implement Circle.
     *  - extends Shape, implements Describable
     *  - one field: final double radius
     *  - area() = pi * r^2
     *  - describe() returns "Circle(r=<radius>)"
     *  - equals()/hashCode(): two Circles are equal iff same radius (and both are Circles —
     *    a Circle must never equal a Rectangle, even with the same area)
     *  - toString() returns describe()
     */
    static class Circle extends Shape implements Describable {
        // TODO: fields, constructor, area(), describe(), equals(), hashCode(), toString()
        public double radius;

        public Circle(double radius) {this.radius = radius;}

        @Override
        double area(){return Math.pow(radius,2) * Math.PI;}

        @Override
        public String describe(){return  "Circle(r="+radius+")";} 

        @Override
        public int hashCode() {
            return Objects.hash(radius);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if (!(obj instanceof Circle c)){return false;}
            return c.radius == radius;
        }

        @Override
        public String toString() {
            return describe();
        }
    
    }

    /** TODO: implement Rectangle.
     *  - extends Shape, implements Describable
     *  - two fields: final double width, height
     *  - area() = width * height
     *  - describe() returns "Rectangle(w=<width>, h=<height>)"
     *  - equals()/hashCode(): two Rectangles are equal iff same width AND height
     *  - toString() returns describe()
     */
    static class Rectangle extends Shape implements Describable {
        // TODO: fields, constructor, area(), describe(), equals(), hashCode(), toString()
        public double width, height;
        public Rectangle(double w, double h){width = w; height = h;};
        public double area(){return width * height;}
        @Override
        public String describe(){
            return "Rectangle(w="+width+", h="+height+")";
        }

         @Override
        public int hashCode() {
            return Objects.hash(width,height);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if (!(obj instanceof Rectangle r)){return false;}
            return r.width == width && r.height == height;
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
