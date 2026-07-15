# 08 — Modern Java (11 → 25)

## Learning outcome and prerequisite

**Outcome:** Model immutable data and closed alternatives with records, sealed types, and exhaustive patterns.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

Features that show you're current, not stuck on Java 8. The baseline examples remain
Java-21-compatible where practical; the final section covers additions through Java 25 LTS.

## `var` — local type inference (Java 10)
```java
var list = new ArrayList<String>();   // like C++ auto, local vars only
```

## Records (Java 16) — transparent immutable data carriers
The single biggest quality-of-life win. Auto-generates constructor, accessors,
`equals`, `hashCode`, `toString`. Think C++ struct + value semantics + `= default`.

```java
record Point(int x, int y) {}

var p = new Point(1, 2);
p.x();                 // accessor (no "get" prefix)
p.equals(new Point(1,2)); // true — value equality for free

// compact canonical constructor for validation
record Range(int lo, int hi) {
    Range {                               // note: no parameter list
        if (lo > hi) throw new IllegalArgumentException("lo > hi");
    }
}
```
Records are implicitly `final`, can't extend, can add methods/static members.

## Sealed types (Java 17) — closed hierarchies
Restrict who can extend/implement — lets the compiler know all subtypes (exhaustive
`switch`, algebraic-data-type style modeling).

```java
sealed interface Shape permits Circle, Rectangle {}
record Circle(double r) implements Shape {}
record Rectangle(double w, double h) implements Shape {}
```

## Pattern matching

### `instanceof` patterns (Java 16)
```java
if (obj instanceof String s && s.length() > 3) {   // s is in scope, already cast
    System.out.println(s.toUpperCase());
}
```

### Switch expressions (Java 14) — arrow form, returns a value, no fall-through
```java
int days = switch (month) {
    case FEB -> 28;
    case APR, JUN, SEP, NOV -> 30;
    default -> 31;
};
```

### Pattern matching in switch + records (Java 21) — the powerful combo
Exhaustive, deconstructing switch over a sealed hierarchy. This is Java's answer to
`std::variant` + `std::visit` / pattern matching.

```java
double area = switch (shape) {
    case Circle c      -> Math.PI * c.r() * c.r();
    case Rectangle(double w, double h) -> w * h;   // record deconstruction pattern
};   // no default needed — sealed + exhaustive
```

Guards with `when`:
```java
String size = switch (shape) {
    case Circle c when c.r() > 10 -> "big circle";
    case Circle c                 -> "small circle";
    case Rectangle r              -> "rectangle";
};
```

## Nested record patterns (Java 21)

Deconstruction patterns nest — you can reach *inside* a record field that's itself a
record, in one pattern, instead of chaining accessor calls:

```java
record Point(int x, int y) {}
record Line(Point from, Point to) {}

static String describe(Object o) {
    return switch (o) {
        case Line(Point(var x1, var y1), Point(var x2, var y2)) ->
            "line from (%d,%d) to (%d,%d)".formatted(x1, y1, x2, y2);   // nested deconstruction
        default -> "not a line";
    };
}
```

## Sequenced collections (Java 21) — uniform first/last access

Before Java 21, "get the first/last element" and "iterate in reverse" had no common
interface — `List` had `get(0)`/`get(size()-1)`, `LinkedHashSet` had neither directly,
`Deque` had its own `getFirst`/`getLast`. `SequencedCollection`, `SequencedSet`, and
`SequencedMap` unify this across `List`, `LinkedHashSet`, `LinkedHashMap`, `ArrayDeque`:

```java
List<Integer> list = new ArrayList<>(List.of(1, 2, 3));
list.getFirst();          // 1 — no more list.get(0)
list.getLast();            // 3 — no more list.get(list.size() - 1)
list.addFirst(0);          // [0, 1, 2, 3]
list.reversed();           // a VIEW, [3, 2, 1, 0] — not a copy, backed by the same list

LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
map.put("a", 1); map.put("b", 2);
map.firstEntry(); map.lastEntry();   // now available uniformly, previously HashMap-family-specific
```

## Local records, enums, and interfaces in methods (Java 16+)

Not just local *classes* (which C++ devs may already expect) — records, enums, and
interfaces can be declared inside a method body too, scoped to that method:

```java
List<String> summarize(List<Order> orders) {
    record Bucket(String status, long count) {}       // local record, scoped to this method
    return orders.stream()
        .collect(Collectors.groupingBy(Order::status, Collectors.counting()))
        .entrySet().stream()
        .map(e -> new Bucket(e.getKey(), e.getValue()))
        .map(b -> b.status() + ": " + b.count())
        .toList();
}
```

## Text blocks (Java 15)
```java
String html = """
    <html>
      <body>hi</body>
    </html>""";
```

## Other niceties
- `Optional`, `List.of`/`Map.of` immutable factories (Java 9).
- `Stream.toList()` (Java 16), `Files.readString`/`writeString` (Java 11).
- Helpful NullPointerExceptions (Java 14) — messages name the exact null variable.
- Virtual threads (Java 21, module 06).

## Java 22–25 additions worth knowing

Java 25 finalized several features introduced after 21:

- **Unnamed variables and patterns** (22): `_` states that a binding is intentionally unused.
- **Scoped values** (25): bounded, immutable context inherited by callees/child threads; prefer
  explicit parameters normally, and use scoped values instead of mutable `ThreadLocal` context
  when framework-style call chains require implicit context.
- **Module import declarations** (25): `import module ...` imports exported packages for concise
  educational/small source files; normal production code often favors explicit imports.
- **Compact source files and instance main methods** (25): remove ceremony for first programs;
  this repository retains named classes so examples also teach conventional project structure.
- **Flexible constructor bodies** (25): statements may validate/prepare values before an explicit
  `super(...)`/`this(...)`, but cannot access the under-construction instance early.

Structured concurrency and primitive patterns are still preview features in Java 25 and are
therefore explained but not required by exercises. Preview APIs require `--enable-preview` and
may change; do not quietly make production compatibility depend on them.

Authoritative inventory: [OpenJDK JDK 25 features](https://openjdk.org/projects/jdk/25/).

**17/21 compatibility:** replace scoped values with carefully cleaned-up `ThreadLocal` or explicit
parameters; use explicit imports, conventional `static main`, and validate arguments before
constructor invocation at a factory/caller boundary.

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). A legacy `instanceof`-chain style `Expr` evaluator
is given (a classic interview "before" state). Your job is the "after":

1. Turn `Shape` (currently a plain abstract class with `instanceof` checks scattered in
   `area()`) into a `sealed interface` permitting `Circle`, `Square`, `Triangle` records.
2. Rewrite `area(Shape)` as an **exhaustive pattern-matching `switch`** with no `default`
   branch — the compiler should refuse to compile if you forget a case.
3. Add a `describe(Shape)` method using a deconstructing record pattern + a `when`
   guard: a `Circle` binds its `radius` component in the pattern itself and describes
   as `"large circle"` when `radius > 10`, otherwise `"circle"`; `Square`/`Triangle`
   just describe as their simple class name, lowercased.

```bash
java -ea 08-modern-java/Exercise.java
```

## Run

```bash
java 08-modern-java/Modern.java
```

## Retrieval practice, hints, and solution

1. When is a record not deeply immutable?
2. How does sealing make a switch safer?
3. Which feature needs a Java 17/21 fallback?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
