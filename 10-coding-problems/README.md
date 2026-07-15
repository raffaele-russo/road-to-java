# 10 — Coding problems, the idiomatic-Java way

## Learning outcome and prerequisite

**Outcome:** Recognize algorithm shapes, implement them idiomatically, and state time/space complexity.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

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

## Practice — unsolved, from scratch

Four more problems, same style as above, but **not solved for you**: each method throws
`UnsupportedOperationException("TODO")` — implement it and get the `assert`s in `main`
to pass.

| File | Pattern | Complexity |
|------|---------|------------|
| `MergeIntervals.java` | sort by start, walk once, extend-or-start-new | O(n log n) |
| `ProductExceptSelf.java` | prefix pass then suffix pass folded into the output array, no division | O(n) time, O(1) extra space |
| `KthLargestElement.java` | size-`k` min-heap (`PriorityQueue`), same idea as `TopKFrequent` | O(n log k) |
| `MaxSubArray.java` | Kadane's algorithm — DP collapsed into a running variable | O(n) time, O(1) space |

```bash
java -ea 10-coding-problems/MergeIntervals.java
java -ea 10-coding-problems/ProductExceptSelf.java
java -ea 10-coding-problems/KthLargestElement.java
java -ea 10-coding-problems/MaxSubArray.java
```

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
| "merge/schedule overlapping ranges" | sort by start + linear merge (`MergeIntervals`) |
| "product/sum of everything except index i", no division | prefix/suffix pass (`ProductExceptSelf`) |
| "kth largest/smallest" | size-k heap (`KthLargestElement`) |
| "max sum contiguous subarray" | Kadane's / running-max DP (`MaxSubArray`) |

## Retrieval practice, hints, and solution

1. State the invariant of a sliding window.
2. When is a size-k heap better than sorting?
3. Which tests expose an off-by-one boundary?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: Reference implementations are in [`solutions/`](solutions/).
