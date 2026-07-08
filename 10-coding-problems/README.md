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

Every file has a `main` with `assert` statements — run with `-ea` to actually enable them
(Java assertions are off by default):

```bash
java -ea 10-coding-problems/TwoSum.java
```

| File | Pattern | Complexity |
|------|---------|------------|
| `TwoSum.java` | HashMap one-pass — the canonical "use a map" pattern | O(n) time |
| `GroupAnagrams.java` | `computeIfAbsent` bucketing + sorted-key signature | O(n·k log k) |
| `GraphBfs.java` | adjacency list (`Map<Integer,List<Integer>>`) + `ArrayDeque` BFS queue | O(V+E) |
| `TopKFrequent.java` | `merge` counting + size-`k` min-heap (`PriorityQueue`) | O(n log k) |
| `ReverseLinkedList.java` | 3-pointer walk, iterative **and** recursive forms | O(n) / O(1)\|O(n) space |
| `BinaryTreeTraversal.java` | recursive DFS (in/pre/post-order) + iterative DFS (stack) + BFS (queue, level order) | O(n) |
| `BinarySearch.java` | classic search + `lowerBound`/first-last-occurrence variant | O(log n) |
| `Sorting.java` | merge sort (stable, guaranteed O(n log n)) + quicksort (in-place, Lomuto partition) | O(n log n) |
| `ValidParentheses.java` | stack-based bracket matching | O(n) |
| `Permutations.java` | backtracking template: choose → recurse → un-choose | O(n!·n) |
| `CoinChange.java` | bottom-up DP, `dp[i]` = fewest coins for amount `i` | O(amount·coins) |
| `ThreeSum.java` | sort + fix one element + two-pointer inward walk, with dedup | O(n²) |
| `LongestSubstringWithoutRepeating.java` | sliding window with a `HashMap<Character,Integer>` of last-seen index | O(n) |

Study the *style*, not just the solution — that's what's being graded once the algorithm
is correct: right collection for the job, clean naming, no off-by-one boundary bugs, and
being able to state the Big-O out loud unprompted.

## Pattern-recognition cheat sheet (what to reach for, given the shape of the problem)

| If the problem says... | Reach for |
|---|---|
| "two numbers that sum to X" / "have I seen this before" | HashMap (`TwoSum`) |
| "group / bucket by some key" | `computeIfAbsent` (`GroupAnagrams`) |
| "shortest path", "levels", "fewest steps" in an unweighted graph/grid | BFS (`GraphBfs`) |
| "top k" / "k most/least" | heap (`PriorityQueue`) sized to k (`TopKFrequent`) |
| sorted array, "find X" | binary search (`BinarySearch`) |
| linked list, reverse/detect cycle/merge | pointer manipulation (`ReverseLinkedList`) |
| tree, "traverse", "depth", "level" | DFS recursion or BFS queue (`BinaryTreeTraversal`) |
| "all combinations/permutations/subsets" | backtracking (`Permutations`) |
| "minimum/maximum ways to reach a target", overlapping subproblems | dynamic programming (`CoinChange`) |
| sorted array, pair/triplet with a target sum | two pointers (`ThreeSum`) |
| "longest/shortest substring/subarray satisfying X" | sliding window (`LongestSubstringWithoutRepeating`) |
| balanced brackets, "most recent unmatched" | stack (`ValidParentheses`) |
