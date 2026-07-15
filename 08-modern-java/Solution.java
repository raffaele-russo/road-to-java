/** Reference solution for Exercise.java. */
public class Solution {
    sealed interface Shape permits Circle, Square, Triangle {}
    record Circle(double radius) implements Shape {}
    record Square(double side) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    static double area(Shape shape) {
        return switch (shape) {
            case Circle(double radius) -> Math.PI * radius * radius;
            case Square(double side) -> side * side;
            case Triangle(double base, double height) -> base * height / 2;
        };
    }

    static String describe(Shape shape) {
        return switch (shape) {
            case Circle(double radius) when radius > 10 -> "large circle";
            case Circle ignored -> "circle";
            case Square ignored -> "square";
            case Triangle ignored -> "triangle";
        };
    }

    public static void main(String[] args) {
        assert area(new Square(4)) == 16;
        assert describe(new Circle(20)).equals("large circle");
        System.out.println("Module 08 reference solution passed.");
    }
}
