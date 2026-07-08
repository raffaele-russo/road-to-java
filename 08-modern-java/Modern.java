/**
 * Records, sealed types, pattern matching for instanceof and switch (incl. record
 * deconstruction + guards), switch expressions, text blocks.
 * Run:  java 08-modern-java/Modern.java
 */
public class Modern {

    public static void main(String[] args) {
        records();
        patternInstanceof();
        switchExpression();
        sealedAndPatternSwitch();
    }

    // ---- records ----
    record Point(int x, int y) {
        // compact canonical constructor for validation
        Point {
            if (x < 0 || y < 0) throw new IllegalArgumentException("negative coord");
        }
        double distanceTo(Point o) { return Math.hypot(x - o.x, y - o.y); }
    }

    static void records() {
        System.out.println("== records ==");
        var a = new Point(0, 0);
        var b = new Point(3, 4);
        System.out.println("  toString  : " + b);            // Point[x=3, y=4]
        System.out.println("  accessor  : b.x()=" + b.x());
        System.out.println("  equals    : " + a.equals(new Point(0, 0))); // true
        System.out.printf("  distance  : %.1f%n", a.distanceTo(b));       // 5.0
    }

    // ---- pattern matching instanceof ----
    static String describe(Object o) {
        if (o instanceof String s) return "string of length " + s.length();
        if (o instanceof Integer i && i > 0) return "positive int " + i;
        return "something else: " + o;
    }

    static void patternInstanceof() {
        System.out.println("\n== instanceof patterns ==");
        for (Object o : new Object[]{"hello", 42, -1, 3.14})
            System.out.println("  " + describe(o));
    }

    // ---- switch expression ----
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    static void switchExpression() {
        System.out.println("\n== switch expression ==");
        for (Day d : Day.values()) {
            String kind = switch (d) {
                case SAT, SUN -> "weekend";
                default -> "weekday";
            };
            System.out.println("  " + d + " -> " + kind);
        }
    }

    // ---- sealed hierarchy + pattern-matching switch (Java 21) ----
    sealed interface Shape permits Circle, Rectangle, Triangle {}
    record Circle(double r) implements Shape {}
    record Rectangle(double w, double h) implements Shape {}
    record Triangle(double base, double height) implements Shape {}

    static double area(Shape s) {
        return switch (s) {                                  // exhaustive: no default needed
            case Circle c -> Math.PI * c.r() * c.r();
            case Rectangle(double w, double h) -> w * h;     // record deconstruction pattern
            case Triangle t when t.base() == 0 -> 0;         // guard
            case Triangle t -> 0.5 * t.base() * t.height();
        };
    }

    static void sealedAndPatternSwitch() {
        System.out.println("\n== sealed + pattern-matching switch ==");
        Shape[] shapes = { new Circle(2), new Rectangle(3, 4), new Triangle(6, 8) };
        for (Shape s : shapes)
            System.out.printf("  %-24s area=%.2f%n", s, area(s));
    }
}
