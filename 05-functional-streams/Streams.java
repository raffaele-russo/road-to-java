import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Lambdas, method references, the Stream API, collectors, primitive streams,
 * and Optional.
 * Run:  java 05-functional-streams/Streams.java
 */
public class Streams {

    record Person(String name, String dept, int salary) {}

    static final List<Person> PEOPLE = List.of(
        new Person("Ada",   "eng",   120),
        new Person("Alan",  "eng",   110),
        new Person("Grace", "sales",  90),
        new Person("Linus", "eng",   130),
        new Person("Kay",   "sales", 100)
    );

    public static void main(String[] args) {
        functionalInterfaces();
        methodReferences();
        streamPipeline();
        collectors();
        primitiveStreams();
        optionals();
    }

    static void functionalInterfaces() {
        System.out.println("== functional interfaces ==");
        Supplier<String> greet = () -> "hi";
        Function<Integer, Integer> square = n -> n * n;
        Predicate<Integer> even = n -> n % 2 == 0;
        BinaryOperator<Integer> add = Integer::sum;
        System.out.println("  supplier=" + greet.get() + " square(5)=" + square.apply(5)
            + " even(4)=" + even.test(4) + " add(3,4)=" + add.apply(3, 4));
    }

    static void methodReferences() {
        System.out.println("\n== method references ==");
        List<String> names = new ArrayList<>(List.of("charlie", "alice", "bob"));
        names.sort(String::compareTo);              // instance method of arbitrary object
        names.forEach(System.out::println);         // bound instance method
        List<Integer> lens = names.stream().map(String::length).toList(); // toList() Java 16+
        System.out.println("  lengths: " + lens);
    }

    static void streamPipeline() {
        System.out.println("\n== stream pipeline (lazy until terminal) ==");
        List<String> result = PEOPLE.stream()
            .filter(p -> p.salary() >= 100)
            .sorted(Comparator.comparingInt(Person::salary).reversed())
            .map(Person::name)
            .toList();
        System.out.println("  earners >= 100, high to low: " + result);
    }

    static void collectors() {
        System.out.println("\n== collectors ==");
        Map<String, List<String>> byDept = PEOPLE.stream()
            .collect(Collectors.groupingBy(Person::dept,
                     Collectors.mapping(Person::name, Collectors.toList())));
        System.out.println("  grouped: " + byDept);

        Map<String, Double> avgByDept = PEOPLE.stream()
            .collect(Collectors.groupingBy(Person::dept,
                     Collectors.averagingInt(Person::salary)));
        System.out.println("  avg salary: " + avgByDept);

        String joined = PEOPLE.stream().map(Person::name)
            .collect(Collectors.joining(", ", "[", "]"));
        System.out.println("  joined: " + joined);

        Map<Boolean, List<String>> partitioned = PEOPLE.stream()
            .collect(Collectors.partitioningBy(p -> p.salary() >= 110,
                     Collectors.mapping(Person::name, Collectors.toList())));
        System.out.println("  >=110? " + partitioned);
    }

    static void primitiveStreams() {
        System.out.println("\n== primitive streams (no boxing) ==");
        int sum = IntStream.rangeClosed(1, 100).sum();
        System.out.println("  1..100 sum = " + sum);
        IntSummaryStatistics stats = PEOPLE.stream().mapToInt(Person::salary).summaryStatistics();
        System.out.printf("  salary min=%d max=%d avg=%.1f%n", stats.getMin(), stats.getMax(), stats.getAverage());
    }

    static void optionals() {
        System.out.println("\n== Optional ==");
        Optional<Person> top = PEOPLE.stream().max(Comparator.comparingInt(Person::salary));
        System.out.println("  highest paid: " + top.map(Person::name).orElse("none"));

        Optional<Person> missing = findByName("Nobody");
        System.out.println("  missing.orElse: " + missing.map(Person::name).orElse("<not found>"));
        missing.ifPresentOrElse(
            p -> System.out.println("  found " + p.name()),
            () -> System.out.println("  (nothing to notify)"));
    }

    static Optional<Person> findByName(String name) {
        return PEOPLE.stream().filter(p -> p.name().equals(name)).findFirst();
    }
}
