import java.util.List;
import java.util.Map;

// Practice: implement each method below with a single stream pipeline (no manual
// for-loops). Run once you think you're done:  java -ea 05-functional-streams/Exercise.java
public class Exercise {

    record Employee(String name, String dept, double salary) {}

    static final List<Employee> EMPLOYEES = List.of(
        new Employee("Alice", "Eng", 120_000),
        new Employee("Bob", "Eng", 110_000),
        new Employee("Cara", "Sales", 90_000),
        new Employee("Dan", "Sales", 95_000),
        new Employee("Eve", "Eng", 130_000)
    );

    /** dept -> average salary in that dept. */
    static Map<String, Double> averageSalaryByDept(List<Employee> employees) {
        throw new UnsupportedOperationException("TODO");
    }

    /** dept -> the single highest-paid Employee in that dept. */
    static Map<String, Employee> highestPaidPerDept(List<Employee> employees) {
        throw new UnsupportedOperationException("TODO");
    }

    /** the n highest-paid employees overall, descending by salary. */
    static List<Employee> topNByPay(List<Employee> employees, int n) {
        throw new UnsupportedOperationException("TODO");
    }

    /** distinct department names, alphabetically sorted, comma-space joined.
     *  deptNamesSorted(EMPLOYEES) -> "Eng, Sales" */
    static String deptNamesSorted(List<Employee> employees) {
        throw new UnsupportedOperationException("TODO");
    }

    public static void main(String[] args) {
        Map<String, Double> avg = averageSalaryByDept(EMPLOYEES);
        assert avg.get("Eng") == 120_000.0 : "got: " + avg.get("Eng");
        assert avg.get("Sales") == 92_500.0 : "got: " + avg.get("Sales");

        Map<String, Employee> top = highestPaidPerDept(EMPLOYEES);
        assert top.get("Eng").name().equals("Eve") : "got: " + top.get("Eng");
        assert top.get("Sales").name().equals("Dan") : "got: " + top.get("Sales");

        List<Employee> top2 = topNByPay(EMPLOYEES, 2);
        assert top2.size() == 2;
        assert top2.get(0).name().equals("Eve") : "got: " + top2;
        assert top2.get(1).name().equals("Alice") : "got: " + top2;

        assert deptNamesSorted(EMPLOYEES).equals("Eng, Sales") : "got: " + deptNamesSorted(EMPLOYEES);

        System.out.println("All good — module 05 exercise complete.");
    }
}
