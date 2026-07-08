# 08 — Modern Java (11 → 21)

Features that show you're current, not stuck on Java 8. All available in Java 21 LTS.

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

## Run

```bash
java 08-modern-java/Modern.java
```
