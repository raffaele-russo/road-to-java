# Learning contract

Every module must answer four questions: **what is the mental model, when should I use it,
how does it fail, and how can I prove my code works?** A long API inventory is not theory.

## Required module structure

1. **Outcomes and prerequisites** — observable abilities, not “understand X”.
2. **Mental model and C++ bridge** — what transfers and what must be unlearned.
3. **Progressive examples** — smallest useful case, realistic composition, boundary case.
4. **Failure modes** — a wrong example, its symptom, its cause, and the correction.
5. **Choice guide** — contract, alternatives, cost, and when not to use the feature.
6. **Production implications** — performance, concurrency, security, compatibility, operations.
7. **Retrieval questions** — answers are below a visible divider; speak before checking.
8. **Applied exercise** — tests state the contract; hints progress from concept to pseudocode.
9. **Reference solution** — complete, runnable, and explains the important decisions.

Examples that intentionally do not compile belong in fenced code blocks labelled
`// does not compile`; CI must never mistake them for runnable files. Examples must avoid
depending on hash iteration order, timing, identity-hash uniqueness, or another JVM detail
that the Java specification does not guarantee.

## Compatibility labels

- **Java 25 baseline** means the repository requires JDK 25 to build.
- **Since Java N** marks the first release containing a feature.
- **17/21 compatibility** gives the replacement needed in common older LTS codebases.
- Preview features are explanatory only and must not be required by exercises or the capstone.

## Exercise feedback

Try `Exercise.java` or the exercise test first. If blocked, use hints in order. Run the
checks again before opening `Solution.java` or the module's `solutions/` directory.
Solutions favor explicit, teachable code and state when production code might choose a
different abstraction.
