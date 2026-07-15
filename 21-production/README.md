# 21 — Production engineering, observability, performance, and delivery

## Outcomes and prerequisites

Operate the capstone: obtain useful telemetry, diagnose runtime behavior from evidence, measure
performance correctly, build a container, and deliver compatible changes through CI.

## Mental model and C++ bridge

Production behavior is a feedback system: workload enters, finite resources saturate, telemetry
describes symptoms, and controlled changes test hypotheses. A JVM removes manual deallocation but
does not remove CPU, memory, thread, connection, queue, or latency budgets. As with native systems,
begin with evidence; unlike a local C++ process, a backend's client-visible tail latency and
dependency health are part of correctness.

## Observability

Logs describe discrete events, metrics aggregate trends, and traces connect work across boundaries.
Use structured logs with timestamp, level, event name, trace/request ID, safe identifiers, and
actionable context. Avoid high-cardinality metric labels such as user/order IDs. RED for services:
rate, errors, duration; USE for resources: utilization, saturation, errors.

Actuator exposes liveness (process cannot continue) separately from readiness (should receive
traffic). A database outage normally affects readiness, not JVM liveness. Protect operational
endpoints; expose only necessary health detail.

## Evidence-first JVM diagnosis

| Symptom | First evidence | Follow-up |
|---|---|---|
| High CPU | process/thread CPU, thread dump/JFR | hot stacks, compilation, allocation |
| Long pauses | latency + GC/JFR events | heap pressure, collector/config, allocation rate |
| Memory growth | heap usage after GC | class histogram, heap dump, retained paths |
| Stuck requests | traces + thread dump | locks, pools, dependency timeouts |
| Startup failure | structured error + condition report | configuration and classpath |

Useful tools: `jcmd`, `jstack`, `jmap`, `jstat`, Java Flight Recorder, Mission Control, and heap
analysers. Heap size or GC flags are not fixes until evidence identifies the constraint.

## Measurement

JIT compilation, dead-code elimination, constant folding, warmup, GC, and CPU frequency make
hand-written timing loops misleading. Use JMH for microbenchmarks and realistic load tests for
system behavior. Track throughput and latency distributions (especially tail latency), allocations,
errors, saturation, and environment. Optimize only a measured bottleneck and remeasure.

The included benchmark returns its result to prevent dead-code elimination, uses forks/warmup,
and varies input size. It is a teaching experiment, not evidence that one operation is your
application bottleneck:

```bash
./mvnw -pl 21-production compile exec:java \
  -Dexec.args='StringBuildingBenchmark -wi 3 -i 5 -f 1'
```

## Containers and shutdown

Build a reproducible layered image, run as non-root, use a small supported JRE image, pin immutable
base versions/digests, set resource limits, keep secrets outside layers, and scan the result. On
SIGTERM, stop accepting work, fail readiness, drain bounded in-flight work, stop consumers, close
pools, and exit before the platform deadline. Handlers must honor interruption.

## CI and compatible delivery

CI runs docs checks, examples, compilation, unit/integration/security tests, static analysis,
package creation, SBOM/image scanning, and smoke tests. Releases use additive database/API/event
changes so old and new versions overlap safely. Roll back application code only when the schema
remains backward compatible; otherwise roll forward.

## Retrieval practice and exercise

Given rising p99 latency with flat CPU, choose telemetry and form hypotheses without changing JVM
flags. Record a short JFR, inspect capstone metrics/traces/log correlation, terminate the container
during work, and document whether shutdown preserved invariants and message processing.

## Progressive example and mastery evidence

The minimal exercise maps symptoms to first evidence; the JMH benchmark demonstrates correct
microbenchmark structure; the capstone composes health, metrics, tracing, containers, and bounded
shutdown. A healthy JVM with an unavailable required database remains live but must not be ready.

```bash
java -ea 21-production/Solution.java
./mvnw -pl 21-production compile exec:java -Dexec.args='StringBuildingBenchmark -wi 3 -i 5 -f 1'
```

Start with [`Exercise.java`](Exercise.java). Hints: choose evidence that distinguishes hypotheses;
readiness answers whether traffic should arrive; map CPU to thread CPU/JFR and memory growth to
heap-after-GC evidence. Passing JMH means it reports results, not that one technique always wins.
