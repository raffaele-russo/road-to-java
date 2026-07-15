# 03 — Collections & Generics

## Learning outcome and prerequisite

**Outcome:** Choose collection contracts and design type-safe generic producer/consumer APIs.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

## The Collections Framework map

```
Iterable
 └── Collection
      ├── List      ordered, indexed, duplicates OK
      │    ├── ArrayList   ← default; backed by array, O(1) random access
      │    └── LinkedList  ← doubly-linked; O(1) ends, rarely worth it
      ├── Set       no duplicates
      │    ├── HashSet     ← default; unordered, O(1)
      │    ├── LinkedHashSet  insertion-ordered
      │    └── TreeSet     sorted (Red-Black tree), O(log n)
      └── Queue / Deque
           ├── ArrayDeque  ← default stack/queue; faster than Stack/LinkedList
           └── PriorityQueue  binary heap

Map (NOT a Collection)
 ├── HashMap        ← default; unordered, O(1)
 ├── LinkedHashMap  insertion/access order (LRU cache!)
 └── TreeMap        sorted keys, O(log n), navigable
```

### C++ STL mapping

| C++ | Java |
|-----|------|
| `std::vector` | `ArrayList` |
| `std::list` | `LinkedList` |
| `std::deque` / stack / queue | `ArrayDeque` |
| `std::unordered_map` | `HashMap` |
| `std::map` | `TreeMap` |
| `std::unordered_set` | `HashSet` |
| `std::set` | `TreeSet` |
| `std::priority_queue` | `PriorityQueue` |

## Idioms

```java
List<String> list = new ArrayList<>();      // program to the interface
Map<String, Integer> counts = new HashMap<>();

// Immutable factory (Java 9+)
List<Integer> ro = List.of(1, 2, 3);        // throws on modification
Map<String,Integer> m = Map.of("a", 1, "b", 2);

// Iterate a map
for (var e : m.entrySet())
    System.out.println(e.getKey() + "=" + e.getValue());

// Merge / compute — the killer methods
counts.merge("apple", 1, Integer::sum);          // count occurrences
counts.computeIfAbsent("k", key -> new ArrayList<>()).add(x); // multimap pattern
counts.getOrDefault("x", 0);
```

**Overload gotcha:** `List<Integer>` has both `remove(int index)` and `remove(Object o)`.
An `int` argument always binds to the `index` overload — to remove the *value* `1`
(not index `1`), box it first.

```java
List<Integer> nums = new ArrayList<>(List.of(5, 1, 9));
nums.remove(1);              // removes INDEX 1 (the value 1) -> [5, 9] here, coincidentally right
nums.remove(Integer.valueOf(9)); // removes the VALUE 9 by boxing -> forces the Object overload
```

**Interview favorite:** frequency counting with `merge`, grouping with
`computeIfAbsent`. These replace verbose "check if key exists then..." code.

## Generics

Java generics ≈ C++ templates in *use*, but implemented by **type erasure**:
- The type parameter exists only at compile time; at runtime `List<String>` and
  `List<Integer>` are both just `List`.
- No `new T()`, no `T.class`, no `new T[n]`, no primitive type args (`List<int>` illegal).
- Templates are duplicated per instantiation (C++); generics are one erased class (Java).

```java
class Box<T> {
    private T value;
    Box(T value) { this.value = value; }
    T get() { return value; }
}

static <T extends Comparable<T>> T max(T a, T b) {   // bounded type param
    return a.compareTo(b) >= 0 ? a : b;
}
```

### Wildcards — PECS

**Producer Extends, Consumer Super:**
- `List<? extends Number>` — read Numbers out (producer). Can't add (except null).
- `List<? super Integer>` — write Integers in (consumer). Reads come out as `Object`.

```java
double sum(List<? extends Number> nums) { ... }   // I only read
void fill(List<? super Integer> sink) { sink.add(1); } // I only write
```

### Erasure gotchas to name in an interview
- `list instanceof List<String>` — illegal; only `List<?>` allowed.
- Overloading `f(List<String>)` and `f(List<Integer>)` — same erasure, won't compile.
- Can't create generic arrays: `new T[10]` → use `(T[]) new Object[10]` or `ArrayList`.

## Iterator & the only safe way to remove during iteration

```java
Iterator<Integer> it = list.iterator();
while (it.hasNext()) {
    int v = it.next();
    if (v % 2 == 0) it.remove();   // the ONLY safe removal during iteration
}
```
Removing from the collection directly (`list.remove(v)`) while a for-each loop iterates
it throws `ConcurrentModificationException` (see the fail-fast note in module 09).
`ListIterator` extends `Iterator` with `hasPrevious`/`previous`/`set`/`add` — two-way
traversal and in-place replacement, only available on `List`.

## `Collections` and `Arrays` utility classes

Static-method toolkits — the closest Java gets to C++'s `<algorithm>` free functions:

```java
Collections.sort(list);
Collections.sort(list, Comparator.reverseOrder());
Collections.reverse(list);
Collections.max(list); Collections.min(list);
Collections.unmodifiableList(list);       // read-only VIEW — backing list can still mutate
Collections.synchronizedList(list);       // legacy coarse-grained thread safety (module 06 has better options)
Collections.emptyList(); Collections.singletonList(x);

int[] arr = {5, 3, 1};
Arrays.sort(arr);
Arrays.toString(arr);                     // arrays don't override toString() themselves
Arrays.asList(1, 2, 3);                   // fixed-SIZE list backed by the array — no add/remove
List<Integer> copy = new ArrayList<>(Arrays.asList(1, 2, 3)); // decouple into a resizable list
```

## `NavigableMap`/`NavigableSet` — the methods that justify choosing `Tree*`

If you reach for `TreeMap`/`TreeSet` in an interview, know these — they're the whole
point of paying O(log n) instead of O(1):

```java
TreeMap<Integer, String> m = new TreeMap<>();
m.put(10, "a"); m.put(20, "b"); m.put(30, "c");

m.floorKey(25);     // 20 — greatest key <= 25
m.ceilingKey(25);   // 30 — smallest key >= 25
m.higherKey(20);    // 30 — strictly greater than 20
m.lowerKey(20);     // 10 — strictly less than 20
m.firstKey(); m.lastKey();
m.headMap(20);      // view of keys < 20
m.tailMap(20, true); // view of keys >= 20 (inclusive flag)
```

## `EnumMap` / `EnumSet` — when the key type is an enum

Array-backed under the hood, so faster and more memory-efficient than `HashMap`/`HashSet`
for enum keys, and iterate in the enum's declaration order.

```java
enum Day { MON, TUE, WED }
EnumMap<Day, String> schedule = new EnumMap<>(Day.class);
EnumSet<Day> weekdays = EnumSet.of(Day.MON, Day.TUE);
```

## Raw types — why they still exist and why to avoid them

A generic type used without its type argument (`List` instead of `List<String>`) compiles
(for backward compatibility with pre-Java-5 code) but loses all compile-time type checking
and produces "unchecked" warnings.

```java
List raw = new ArrayList();      // raw type — avoid in new code
raw.add("a");
raw.add(42);                     // compiles! no type safety at all
for (Object o : raw) { }         // you're back to casting everything yourself
```

## Generic methods with multiple bounds

```java
static <T extends Comparable<T> & Cloneable> T pick(T a, T b) {  // T must satisfy BOTH
    return a.compareTo(b) >= 0 ? a : b;
}
```
At most one **class** bound, but any number of **interface** bounds; the class (if any)
must come first.

## Comparable vs Comparator

```java
list.sort(Comparator.comparingInt(String::length)
                     .thenComparing(Comparator.naturalOrder()));
// Comparable<T>: natural ordering, implemented by the class (compareTo)
// Comparator<T>: external ordering, passed in
```

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). Two classic interview builds:

1. `MultiMap<K, V>` — a generic wrapper around `Map<K, List<V>>` with `put`/`get`/`size`,
   built with `computeIfAbsent` (no manual null-checking).
2. `LruCache<K, V>` — a fixed-capacity least-recently-used cache. Extend `LinkedHashMap`
   in access-order mode and override `removeEldestEntry` (the idiom named in module 09's
   `HashMap` Q&A) rather than hand-rolling a doubly-linked list.

```bash
java -ea 03-collections-generics/Exercise.java
```

## Run

```bash
java 03-collections-generics/Collections.java
java 03-collections-generics/Generics.java
```

## Retrieval practice, hints, and solution

1. Which contract—not implementation name—does the caller need?
2. Derive PECS for a copy operation.
3. Which mutations invalidate a hash-based lookup?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
