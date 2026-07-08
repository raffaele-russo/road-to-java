import java.util.*;

/**
 * Breadth-first shortest path (unweighted) on an adjacency-list graph.
 * Pattern: Map<Integer,List<Integer>> adjacency, ArrayDeque as the FIFO queue,
 * a visited set, and a parent map to reconstruct the path.
 * Run:  java 10-coding-problems/GraphBfs.java
 */
public class GraphBfs {

    static class Graph {
        private final Map<Integer, List<Integer>> adj = new HashMap<>();
        void addEdge(int u, int v) {                       // undirected
            adj.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
            adj.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
        }
        List<Integer> neighbors(int u) { return adj.getOrDefault(u, List.of()); }
    }

    static List<Integer> shortestPath(Graph g, int start, int goal) {
        Queue<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        Map<Integer, Integer> parent = new HashMap<>();

        queue.offer(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            int node = queue.poll();
            if (node == goal) return reconstruct(parent, start, goal);
            for (int next : g.neighbors(node)) {
                if (visited.add(next)) {                   // add() returns false if present
                    parent.put(next, node);
                    queue.offer(next);
                }
            }
        }
        return List.of();                                  // unreachable
    }

    static List<Integer> reconstruct(Map<Integer, Integer> parent, int start, int goal) {
        LinkedList<Integer> path = new LinkedList<>();
        for (Integer at = goal; at != null; at = parent.get(at)) {
            path.addFirst(at);
            if (at == start) break;
        }
        return path;
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge(1, 2); g.addEdge(1, 3); g.addEdge(2, 4);
        g.addEdge(3, 4); g.addEdge(4, 5);
        List<Integer> path = shortestPath(g, 1, 5);
        System.out.println("shortest path 1->5: " + path);  // [1, 2, 4, 5] or [1, 3, 4, 5]
        assert path.get(0) == 1 && path.get(path.size() - 1) == 5;
        assert path.size() == 4 : "expected length-4 path";
        System.out.println("OK (run with -ea to enable assertions)");
    }
}
