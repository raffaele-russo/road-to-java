# 05 — Functional programming & the Stream API

## Learning outcome and prerequisite

**Outcome:** Explain stream laziness and build readable, bounded pipelines and collectors.

Follow the repository [learning contract](../LEARNING-CONTRACT.md): form a mental model,
run and change the demonstrations, explain the failure modes, complete the exercise without
the solution open, and answer retrieval questions aloud. Prerequisite: complete the earlier
modules in the same roadmap track unless this module states otherwise.

The single biggest "modern Java" area interviewers probe. If you know C++ lambdas and
`<algorithm>` / ranges, this will feel familiar but more fluent.

## Lambdas & functional interfaces

A lambda is an instance of a **functional interface** (exactly one abstract method,
`@FunctionalInterface`). No captures-by-reference — lambdas capture *effectively final*
variables by value (references to objects, so you can still mutate the object).

```java
Runnable r = () -> System.out.println("run");
Comparator<String> byLen = (a, b) -> a.length() - b.length();
```

### The core functional interfaces (`java.util.function`)

| Interface | Signature | C++ analog |
|-----------|-----------|------------|
| `Supplier<T>` | `() -> T` | factory |
| `Consumer<T>` | `T -> void` | sink |
| `Function<T,R>` | `T -> R` | transform |
| `BiFunction<T,U,R>` | `(T,U) -> R` | |
| `Predicate<T>` | `T -> boolean` | filter test |
| `UnaryOperator<T>` | `T -> T` | |
| `BinaryOperator<T>` | `(T,T) -> T` | reduce op |

## Method references — 4 kinds

```java
String::toUpperCase       // instance method of arbitrary object:  s -> s.toUpperCase()
System.out::println       // instance method of a bound object:    x -> System.out.println(x)
Integer::parseInt         // static method:                        s -> Integer.parseInt(s)
ArrayList::new            // constructor:                          () -> new ArrayList<>()
```

## Streams — declarative data pipelines

A `Stream` is a lazy pipeline: **source → intermediate ops → terminal op**. Nothing runs
until the terminal op. Streams are single-use and don't mutate the source.

```
list.stream()                       // source
    .filter(x -> x > 0)             // intermediate (lazy)
    .map(x -> x * x)                // intermediate (lazy)
    .sorted()                       // intermediate (lazy)
    .collect(Collectors.toList());  // terminal — triggers execution
```

### Common operations

| Category | Ops |
|----------|-----|
| Intermediate | `filter`, `map`, `flatMap`, `distinct`, `sorted`, `limit`, `skip`, `peek` |
| Terminal | `collect`, `forEach`, `reduce`, `count`, `min`/`max`, `anyMatch`/`allMatch`, `findFirst` |

### Collectors — the workhorse

```java
.collect(Collectors.toList())
.collect(Collectors.toSet())
.collect(Collectors.joining(", ", "[", "]"))
.collect(Collectors.groupingBy(Person::dept))                       // Map<Dept, List<Person>>
.collect(Collectors.groupingBy(Person::dept, Collectors.counting())) // Map<Dept, Long>
.collect(Collectors.partitioningBy(n -> n % 2 == 0))                // Map<Boolean, List<>>
.collect(Collectors.toMap(Person::id, p -> p))
```

### Primitive streams (avoid boxing)

```java
IntStream.rangeClosed(1, 100).sum();
IntStream.range(0, arr.length).mapToObj(i -> ...);
list.stream().mapToInt(Integer::intValue).average();
```

### Parallel streams

`stream().parallel()` splits across the common ForkJoinPool. Only helps for large,
CPU-bound, stateless, side-effect-free work. Easy to misuse — measure first.

## Composing functions & predicates

`Function`/`Predicate` aren't just single lambdas — they compose, which is a favorite
"show me you know the API beyond the basics" interview probe:

```java
Function<Integer, Integer> times2 = x -> x * 2;
Function<Integer, Integer> plus3  = x -> x + 3;
times2.andThen(plus3).apply(5);   // (5*2)+3  = 13 — times2 runs first
times2.compose(plus3).apply(5);   // (5+3)*2  = 16 — plus3 runs first

Predicate<String> isLong  = s -> s.length() > 5;
Predicate<String> hasA    = s -> s.contains("a");
isLong.and(hasA).test("banana");   // true — both must hold
isLong.negate().test("hi");        // true
```

## `Comparator` — chaining and reversing

```java
Comparator<Person> byAge = Comparator.comparingInt(Person::age);
list.sort(byAge.thenComparing(Person::name));     // tie-break by name
list.sort(byAge.reversed());                       // oldest first
list.sort(Comparator.comparing(Person::name, Comparator.reverseOrder())); // reverse alpha
```

## `Stream.iterate` / `Stream.generate` — building a stream from nothing

```java
Stream.iterate(1, n -> n * 2).limit(5).toList();          // [1, 2, 4, 8, 16] — infinite, must limit
Stream.iterate(1, n -> n < 100, n -> n * 2).toList();      // bounded form (Java 9+) — no limit() needed
Stream.generate(Math::random).limit(3).toList();           // supplier-based, no state between calls
```

## Short-circuiting terminal operations

`anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`, and `limit` stop processing
as soon as the answer is known — they don't necessarily traverse the whole source.

```java
boolean hasNegative = hugeList.stream().anyMatch(x -> x < 0);  // stops at the first match
Optional<Integer> first = hugeList.stream().filter(x -> x > 100).findFirst(); // stops early
```

## `Collectors.teeing` (Java 12+) — two collectors, one pass

```java
var result = list.stream().collect(Collectors.teeing(
    Collectors.summingInt(Integer::intValue),
    Collectors.counting(),
    (sum, count) -> sum / (double) count   // combine both results — a single-pass average
));
```

## `Collectors.summarizingInt`/`averagingInt`/etc. — statistics without a manual reduce

```java
IntSummaryStatistics stats = employees.stream().collect(Collectors.summarizingInt(Employee::salary));
stats.getMin(); stats.getMax(); stats.getAverage(); stats.getSum(); stats.getCount();
```

## Optional — no more null

`Optional<T>` models "maybe a value" and is the idiomatic return for "might not find one"
(instead of returning `null` or throwing). Similar spirit to `std::optional`.

```java
Optional<User> u = repo.findById(id);
String name = u.map(User::name).orElse("anonymous");
u.ifPresent(user -> notify(user));
User required = u.orElseThrow(() -> new NotFoundException(id));
```
**Don't**: call `.get()` without checking, use it for fields, or return `null` from an
`Optional`-typed method.

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). A fixed `List<Employee>` record list is given.
Implement four analysis methods, each with a **one-line stream pipeline** (no manual
loops):

1. `averageSalaryByDept(List<Employee>)` → `Map<String, Double>` — `groupingBy` +
   `averagingDouble`.
2. `highestPaidPerDept(List<Employee>)` → `Map<String, Employee>` — `groupingBy` +
   a `Collectors.maxBy` downstream (unwrap the `Optional`).
3. `topNByPay(List<Employee>, int n)` → `List<Employee>` — sort descending, take `n`.
4. `deptNamesSorted(List<Employee>)` → `String` — distinct department names, sorted,
   joined with `", "`.

```bash
java -ea 05-functional-streams/Exercise.java
```

## Run

```bash
java 05-functional-streams/Streams.java
```

## Retrieval practice, hints, and solution

1. Which operations are lazy and when does work begin?
2. Why can side effects make a parallel stream incorrect?
3. When is a loop clearer than a collector?

Hints: first name the governing contract; then construct the smallest counterexample; finally
write the invariant or pseudocode before reaching for an API. Run the checks after each step.

Reference feedback: [`Solution.java`](Solution.java)
