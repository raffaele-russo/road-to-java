import java.util.*;

/**
 * The Collections Framework and its power methods (merge, computeIfAbsent, ...).
 * Note: class named CollectionsDemo would clash with java.util.Collections; we
 * fully-qualify where needed. Run:  java 03-collections-generics/Collections.java
 */
public class Collections {

    public static void main(String[] args) {
        lists();
        setsAndMaps();
        frequencyCounting();
        multimapPattern();
        sortingWithComparators();
        queuesAndHeaps();
    }

    static void lists() {
        System.out.println("== List ==");
        List<String> list = new ArrayList<>(List.of("b", "a", "c"));
        list.add("d");
        java.util.Collections.sort(list);
        System.out.println("  sorted: " + list);
        System.out.println("  get(0): " + list.get(0) + "  size: " + list.size());
    }

    static void setsAndMaps() {
        System.out.println("\n== Set / Map ==");
        Set<Integer> hash = new HashSet<>(List.of(3, 1, 2, 2, 1));
        Set<Integer> tree = new TreeSet<>(List.of(3, 1, 2, 2, 1));
        System.out.println("  HashSet (dedup, unordered): " + hash);
        System.out.println("  TreeSet (dedup, sorted)   : " + tree);

        Map<String, Integer> ages = new HashMap<>();
        ages.put("Ada", 36);
        ages.put("Alan", 41);
        System.out.println("  getOrDefault missing: " + ages.getOrDefault("Nobody", -1));
        ages.forEach((k, v) -> System.out.println("    " + k + " -> " + v));
    }

    static void frequencyCounting() {
        System.out.println("\n== frequency counting with merge ==");
        String[] words = {"apple", "banana", "apple", "cherry", "banana", "apple"};
        Map<String, Integer> counts = new HashMap<>();
        for (String w : words) counts.merge(w, 1, Integer::sum);
        System.out.println("  " + counts); // {banana=2, cherry=1, apple=3}
    }

    static void multimapPattern() {
        System.out.println("\n== multimap with computeIfAbsent ==");
        Map<Integer, List<String>> byLen = new HashMap<>();
        for (String w : List.of("a", "bb", "cc", "ddd"))
            byLen.computeIfAbsent(w.length(), k -> new ArrayList<>()).add(w);
        System.out.println("  " + byLen); // {1=[a], 2=[bb, cc], 3=[ddd]}
    }

    static void sortingWithComparators() {
        System.out.println("\n== comparators ==");
        List<String> names = new ArrayList<>(List.of("bob", "al", "charlie", "di"));
        names.sort(Comparator.comparingInt(String::length)
                             .thenComparing(Comparator.naturalOrder()));
        System.out.println("  by length then alpha: " + names);
        names.sort(Comparator.reverseOrder());
        System.out.println("  reverse alpha       : " + names);
    }

    static void queuesAndHeaps() {
        System.out.println("\n== Deque + PriorityQueue ==");
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(1); stack.push(2); stack.push(3);
        System.out.println("  stack pop: " + stack.pop() + " (LIFO)");

        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        minHeap.addAll(List.of(5, 1, 3, 2, 4));
        StringBuilder sb = new StringBuilder("  heap poll order: ");
        while (!minHeap.isEmpty()) sb.append(minHeap.poll()).append(' ');
        System.out.println(sb);
    }
}
