# 10 — Coding problems, the idiomatic-Java way

For a C++ dev, the algorithms are the easy part — the goal here is writing them the way a
Java interviewer expects: the right collections, the power methods, clean streams.

## The interview toolkit

| Task | C++ reflex | Idiomatic Java |
|------|-----------|----------------|
| Count frequencies | `map[x]++` | `map.merge(x, 1, Integer::sum)` |
| Group into buckets | manual insert | `map.computeIfAbsent(k, x -> new ArrayList<>()).add(v)` |
| Default lookup | check then `[]` | `map.getOrDefault(k, dflt)` |
| Min/max heap | `priority_queue` | `PriorityQueue<>(Comparator...)` |
| Stack | `std::stack` | `ArrayDeque` (`push`/`pop`/`peek`) |
| Queue / BFS | `std::queue` | `ArrayDeque` (`offer`/`poll`) |
| Sorted set/map | `std::set/map` | `TreeSet`/`TreeMap` (`floor`, `ceiling`, `first`) |
| Sort with key | lambda comparator | `Comparator.comparing(...)` |
| Char ↔ int | implicit | `c - 'a'`, `(char)(i + 'a')` |
| Growable array | `vector` | `ArrayList` (or `int[]` if size known) |
| 2D grid | `vector<vector>` | `int[][]` (jagged allowed) |

## String / char tips
- `s.charAt(i)`, `s.length()`, `s.substring(a, b)`, `s.toCharArray()`.
- `char` arithmetic works: `s.charAt(i) - 'a'` gives 0–25.
- Build strings with `StringBuilder`, never `+=` in a loop.
- `s.split("\\s+")`, `String.join(",", list)`.

## Big-O awareness the interviewer wants stated
- `ArrayList.get` O(1), `contains` O(n); `HashSet.contains` O(1).
- `TreeMap` ops O(log n) but keep keys sorted (use when you need order/range).
- `PriorityQueue` offer/poll O(log n), peek O(1).

## Worked examples (all runnable)

Each has a `main` with assertions so you can confirm correctness:

```bash
java 10-coding-problems/TwoSum.java
java 10-coding-problems/GroupAnagrams.java
java 10-coding-problems/GraphBfs.java
java 10-coding-problems/TopKFrequent.java
```

- **TwoSum** — HashMap one-pass; the canonical "use a map" pattern.
- **GroupAnagrams** — `computeIfAbsent` bucketing + sorted-key trick.
- **GraphBfs** — adjacency list with `Map<Integer,List<Integer>>`, `ArrayDeque` queue.
- **TopKFrequent** — `merge` counting + `PriorityQueue` min-heap of size k.

Study the *style*, not just the solution — that's what's being graded once the algorithm
is correct.
