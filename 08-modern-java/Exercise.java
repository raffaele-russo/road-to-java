// Practice: modernize a legacy shape hierarchy into sealed types + pattern-matching
// switch. This file does NOT compile yet — main() already refers to Circle/Square/
// Triangle as Shape, which only type-checks once you've done the TODOs below.
// Run once you think you're done:  java -ea 08-modern-java/Exercise.java
public class Exercise {

    // TODO: turn this into `sealed interface Shape permits Circle, Square, Triangle {}`
    interface Shape {
    }

    // TODO: make each of these implement Shape (part of what makes the interface sealed
    // meaningful — the permits list must name exactly these three).
    record Circle(double radius) {}
    record Square(double side) {}
    record Triangle(double base, double height) {}

    /** TODO: exhaustive pattern-matching switch over the sealed Shape hierarchy.
     *  No `default` branch — once Shape is sealed, the compiler enforces exhaustiveness,
     *  so forgetting a case here should fail to COMPILE, not misbehave at runtime. */
    static double area(Shape s) {
        throw new UnsupportedOperationException("TODO");
    }

    /** TODO: pattern-matching switch with a deconstructing pattern + `when` guard:
     *  - Circle with radius() > 10 -> "large circle"
     *  - any other Circle          -> "circle"
     *  - Square                    -> "square"
     *  - Triangle                  -> "triangle" */
    static String describe(Shape s) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        Shape circleSmall = new Circle(2);
        Shape circleBig = new Circle(20);
        Shape square = new Square(4);
        Shape triangle = new Triangle(3, 6);

        assert Math.abs(area(circleSmall) - Math.PI * 4) < 1e-9 : "circle area formula: pi*r^2";
        assert area(square) == 16.0 : "square area: side*side";
        assert area(triangle) == 9.0 : "triangle area: 0.5*base*height";

        assert describe(circleSmall).equals("circle") : "got: " + describe(circleSmall);
        assert describe(circleBig).equals("large circle") : "got: " + describe(circleBig);
        assert describe(square).equals("square") : "got: " + describe(square);
        assert describe(triangle).equals("triangle") : "got: " + describe(triangle);

        System.out.println("All good — module 08 exercise complete.");
    }
}
