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

## Casting & numeric promotion

- **Widening** (small → big, e.g. `int` → `long`, `float` → `double`) is implicit and safe.
- **Narrowing** (big → small, e.g. `long` → `int`, `double` → `int`) needs an explicit cast
  and can silently lose data — no compiler warning, unlike some C++ toolchains.
- In any binary numeric expression, operands are **promoted** to the widest type present
  (at least `int` — so `byte + byte` is an `int`, not a `byte`).

```java
int i = (int) 3.99;        // 3 — truncates toward zero, doesn't round
byte b = (byte) 200;       // -56 — silent overflow, wraps around (two's complement)
long l = 10;                // widening, no cast needed
double d = l;                // widening, no cast needed

byte b1 = 10, b2 = 20;
// byte b3 = b1 + b2;       // compile error — b1 + b2 is promoted to int
int sum = b1 + b2;          // fine
```

**Interview gotcha:** integer division truncates. `5 / 2 == 2`, not `2.5`. Force floating
point by casting an operand: `5 / (double) 2 == 2.5`.

## Bitwise & shift operators

Java has all of C++'s bitwise operators, but shifts are defined precisely (no UB):

```java
int a = 5 & 3;   // AND -> 1
int b = 5 | 2;   // OR  -> 7
int c = 5 ^ 1;   // XOR -> 4
int d = ~5;      // NOT -> -6 (two's complement)
int e = 1 << 4;  // left shift  -> 16
int f = -8 >> 1; // signed right shift, sign-extends -> -4
int g = -8 >>> 1; // UNSIGNED right shift, zero-fills -> 2147483644 (no C++ equivalent operator)
```

`>>>` (unsigned/logical right shift) is Java-specific — C++ has no dedicated operator for
it because C++ picks arithmetic vs. logical shift based on whether the type is signed.

## Ternary operator & classic `switch`

```java
int max = (a > b) ? a : b;   // same as C++

switch (day) {               // classic form — falls through without break
    case MONDAY:
    case TUESDAY:
        System.out.println("early week");
        break;                // forgetting this is a classic bug — falls into next case
    default:
        System.out.println("later");
}
```
See module 08 for the modern arrow-form `switch` that fixes fall-through entirely.

## String pool & interning (deeper than module 00)

String literals are interned automatically into a shared pool; `new String(...)` forces a
fresh heap object even if an identical literal already exists.

```java
String a = "hi";
String b = "hi";
a == b;                       // true — same pooled literal

String c = new String("hi");
a == c;                       // false — c is a distinct heap object
a == c.intern();              // true — intern() looks up (or adds to) the pool
```
**Interview one-liner:** the pool exists because strings are immutable, so sharing is
always safe — this is also why `String` is the textbook example for why immutability
enables safe sharing without defensive copies.

## Practice exercise — from scratch

Open [`Exercise.java`](Exercise.java). Four methods, `String`/`char[]`/control-flow only
(no `Collections` — that's module 03), each throwing `UnsupportedOperationException("TODO")`.
Implement each and get every `assert` in `main` to pass:

1. `isPalindrome(String s)` — case-insensitive, ignore non-alphanumeric characters.
2. `reverseWords(String s)` — `"the sky is blue"` → `"blue is sky the"`, using only
   `char[]`/`StringBuilder`, not `String.split` + `Collections.reverse`.
3. `sumDigits(int n)` — sum of the decimal digits of `n` (handle negatives).
4. `runLengthEncode(String s)` — `"aaabbc"` → `"a3b2c1"`.

```bash
java -ea 01-basics/Exercise.java
```

## Run

```bash
java 01-basics/Basics.java
```
