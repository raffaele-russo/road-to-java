# 05 — Functional programming & the Stream API

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

## Run

```bash
java 05-functional-streams/Streams.java
```
