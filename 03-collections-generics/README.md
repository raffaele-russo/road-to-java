# 03 — Collections & Generics

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

## Comparable vs Comparator

```java
list.sort(Comparator.comparingInt(String::length)
                     .thenComparing(Comparator.naturalOrder()));
// Comparable<T>: natural ordering, implemented by the class (compareTo)
// Comparator<T>: external ordering, passed in
```

## Run

```bash
java 03-collections-generics/Collections.java
java 03-collections-generics/Generics.java
```
