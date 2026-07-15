# Learning contract

Every module must answer four questions: **what is the mental model, when should I use it,
how does it fail, and how can I prove my code works?** A long API inventory is not theory.

## Required module structure and evidence

1. **Outcomes and prerequisites** — observable abilities, not “understand X”.
2. **Mental model and C++ bridge** — what transfers and what must be unlearned.
3. **Progressive examples** — smallest useful case, realistic composition, boundary case.
4. **Failure modes** — a wrong example, its symptom, its cause, and the correction.
5. **Choice guide** — contract, alternatives, cost, and when not to use the feature.
6. **Production implications** — performance, concurrency, security, compatibility, operations.
7. **Retrieval questions** — answers are below a visible divider; speak before checking.
8. **Applied exercise** — tests state the contract; hints progress from concept to pseudocode.
9. **Reference solution** — complete, runnable, and explains the important decisions.

The headings may be combined when that improves the narrative, but the evidence may not be
omitted. Each module README ends with a **Mastery evidence** block linking to its demonstration,
practice, automated verification, and assessment. A module is not complete merely because its
README mentions a topic.

## Example ladder

Every major concept is taught through the same three-step ladder:

1. **Minimal** — the smallest code that exposes the governing contract and deterministic output.
2. **Composition** — the concept used with another API in a realistic backend-shaped example.
3. **Boundary/failure** — incorrect or adversarial input, the observed symptom, its cause, and fix.

Examples state what to predict before execution and what evidence proves the explanation. Each
ladder identifies the C++ intuition that transfers or must be unlearned. Prefer one example that
can be modified over several disconnected snippets.

## Mastery evidence

Every required competency has five links in [`CURRICULUM-MAP.md`](CURRICULUM-MAP.md): theory,
demonstration, practice, automated proof, and assessment. Evidence has these meanings:

- **Explain** — derive behavior from a mental model rather than repeat an API fact.
- **Implement** — complete a learner-owned exercise from a stated contract.
- **Test** — select observable behavior, including a negative or boundary case.
- **Diagnose** — identify the violated contract from a symptom and choose useful evidence.
- **Judge** — compare alternatives and state when not to use the demonstrated technique.

The normal build verifies demonstrations and reference solutions, never learner TODOs. A manual
diagnostic is valid evidence only when the README gives a deterministic setup, command, expected
observation, and cleanup instructions.

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

Hints use three levels: (1) name the contract, (2) describe the invariant or counterexample,
(3) give pseudocode without copying the solution. Retrieval answers stay below a visible divider.
