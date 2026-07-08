# 01 — Basics: types, references, strings, arrays

## Primitive types (the only non-objects)

| Type | Size | Range / notes | C++ analog |
|------|------|---------------|------------|
| `byte` | 8-bit | -128..127 | `int8_t` |
| `short` | 16-bit | | `int16_t` |
| `int` | 32-bit | default integer literal type | `int32_t` |
| `long` | 64-bit | literal suffix `L` | `int64_t` |
| `float` | 32-bit | suffix `f` | `float` |
| `double` | 64-bit | default float literal type | `double` |
| `char` | 16-bit | **UTF-16 code unit**, unsigned | `char16_t` |
| `boolean` | JVM-defined | `true`/`false` only, no int conversion | `bool` |

Key differences from C++:
- **All sizes are fixed** by the spec — no platform variance, no `size_t`.
- **No `unsigned`** types (except `char`). Java 8+ adds `Integer.divideUnsigned` etc. helpers.
- `boolean` is **not** an integer; `if (x)` requires `x` to be boolean.
- Everything else is a reference type (objects, arrays, `String`).

## Wrapper types & autoboxing

Each primitive has a boxed class: `int`↔`Integer`, `double`↔`Double`, etc.
Collections can only hold objects, so `List<Integer>`, never `List<int>`.

```java
List<Integer> nums = new ArrayList<>();
nums.add(5);            // autobox: int -> Integer
int first = nums.get(0); // auto-unbox: Integer -> int
```

**Gotchas:**
- Unboxing a `null` Integer throws `NullPointerException`.
- `==` on boxed types compares references (see the caching gotcha in module 00).
- Boxing in hot loops costs allocations — prefer primitives / `IntStream`.

## Strings

- Immutable. Every "modification" creates a new object.
- Use `StringBuilder` for repeated concatenation (like `std::string` mutation).
- `+` is the only overloaded operator in Java.
- Compare with `.equals()`, never `==`.
- Text blocks (Java 15+): `"""..."""` for multi-line literals.

```java
String s = "a" + "b";              // fine for a few
StringBuilder sb = new StringBuilder();
for (int i = 0; i < 1000; i++) sb.append(i); // fine for loops
String result = sb.toString();
```

## Arrays

- Fixed length, know their own size via `.length` (a field, not a method).
- Bounds-checked at runtime → `ArrayIndexOutOfBoundsException`, never UB.
- Declared `int[] a` (preferred) or `int a[]` (C-style, discouraged).
- Multidimensional arrays are arrays-of-arrays (can be jagged), not contiguous blocks.

```java
int[] a = new int[5];          // zero-initialized
int[] b = {1, 2, 3};           // literal
int[][] grid = new int[3][4];  // 3 rows, 4 cols
```

For dynamic sizing, use `ArrayList` (module 03), not arrays.

## Control flow — mostly familiar

- `if/else`, `for`, `while`, `do-while` as in C++.
- Enhanced for-each: `for (String x : list)`.
- `switch` — traditional (with fall-through) **and** modern arrow form (module 08).
- No `goto` (reserved keyword, unused). Labeled `break`/`continue` exist.

## `var` — local type inference (Java 10+)

```java
var list = new ArrayList<String>();  // inferred ArrayList<String>
var count = 5;                       // int
```
Like C++ `auto`, but **local variables only** — not fields, params, or return types.

## Run

```bash
java 01-basics/Basics.java
```
