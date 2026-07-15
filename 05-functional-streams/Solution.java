import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/** Reference solution for Exercise.java. */
public class Solution {
    record Employee(String name, String dept, double salary) {}

    static Map<String, Double> averageSalaryByDept(List<Employee> employees) {
        return employees.stream().collect(Collectors.groupingBy(
            Employee::dept, Collectors.averagingDouble(Employee::salary)));
    }

    static Map<String, Employee> highestPaidPerDept(List<Employee> employees) {
        return employees.stream().collect(Collectors.toMap(Employee::dept, e -> e,
            BinaryOperator.maxBy(Comparator.comparingDouble(Employee::salary))));
    }

    static List<Employee> topNByPay(List<Employee> employees, int n) {
        if (n < 0) throw new IllegalArgumentException("n must be non-negative");
        return employees.stream()
            .sorted(Comparator.comparingDouble(Employee::salary).reversed())
            .limit(n).toList();
    }

    static String deptNamesSorted(List<Employee> employees) {
        return employees.stream().map(Employee::dept).distinct().sorted()
            .collect(Collectors.joining(", "));
    }

    public static void main(String[] args) {
        var people = List.of(new Employee("Ada", "Eng", 120), new Employee("Lin", "Eng", 100));
        assert averageSalaryByDept(people).get("Eng") == 110;
        assert highestPaidPerDept(people).get("Eng").name().equals("Ada");
        System.out.println("Module 05 reference solution passed.");
    }
}
