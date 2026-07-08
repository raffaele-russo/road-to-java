# 13 — Spring Basics (DI/IoC)

"Java interview" and "Spring" are practically synonymous in most backend job markets.
This module is a **real, runnable** Spring (core `spring-context`, not full Boot)
project — not just theory — so you can see dependency injection actually happen.

## Setup

```bash
cd 13-spring-basics
mvn test              # runs SpringContextTest — proves the wiring works
mvn compile exec:java # runs Main — prints a live walkthrough of every concept
```

## Inversion of Control (IoC) — the core idea

In plain Java/C++, an object typically constructs its own dependencies:
```java
class Greeter { MessageService svc = new EmailMessageService(); } // Greeter controls creation
```
With IoC, that control is inverted: a **container** builds objects and hands them their
dependencies. The object just declares what it needs (usually via constructor params).
This is **Dependency Injection (DI)** — IoC applied to wiring dependencies.

Why it matters for testing (module 12): a class that receives its dependencies can be
unit-tested with fakes/mocks, no container needed — see `Greeter` in this module,
constructed directly with a mock in a plain JUnit test elsewhere.

## The annotations you must recognize instantly

| Annotation | What it does |
|------------|---------------|
| `@Component` | generic Spring-managed bean |
| `@Service` | semantic alias for `@Component` — business logic layer |
| `@Repository` | semantic alias — data access layer; also translates DB exceptions |
| `@Controller` / `@RestController` | web layer; `@RestController` = `@Controller` + `@ResponseBody` on every method |
| `@Configuration` | class containing `@Bean` factory methods |
| `@Bean` | manually register a bean (for types you don't own / can't annotate) |
| `@Autowired` | inject a dependency — prefer **constructor injection** (no annotation needed if there's a single constructor) over field injection |
| `@Qualifier("name")` | disambiguate when multiple beans match a type |
| `@Primary` | the default choice when multiple beans match and no `@Qualifier` is given |
| `@Value("${prop:default}")` | inject a config property (with a fallback default) |
| `@Scope("singleton"\|"prototype")` | singleton (default, one shared instance) vs prototype (new instance every request) |
| `@PostConstruct` / `@PreDestroy` | lifecycle hooks — closest thing to a constructor "finish" step / a destructor |
| `@ComponentScan` | tells Spring which packages to scan for `@Component` classes |

## Constructor injection > field injection (know why)

```java
// PREFERRED — dependencies are explicit, final, and testable without Spring:
@Component
class Greeter {
    private final MessageService svc;
    Greeter(MessageService svc) { this.svc = svc; }   // Spring resolves this automatically
}

// AVOID — hides dependencies, can't be final, breaks without Spring/reflection:
@Component
class Greeter {
    @Autowired private MessageService svc;
}
```
Constructor injection also fails fast at startup (missing bean = context won't boot)
instead of a runtime `NullPointerException` later.

## Bean scopes

- **singleton** (default): one instance per container, shared everywhere.
- **prototype**: a new instance every time it's requested — see `RequestContext.java`.
- Web scopes (`request`, `session`) exist only in a web application context (Spring MVC).

## Bean lifecycle order

```
constructor -> dependency injection -> @PostConstruct -> ready for use -> @PreDestroy (on context.close())
```
See `LifecycleBean.java` — run `Main.java` and watch the print order.

## Spring Boot — what changes on top of core Spring

Spring Boot (not exercised here to keep this module dependency-light and fast) adds:
- **Auto-configuration**: sensible beans wired automatically from the classpath
  (add `spring-boot-starter-web` → an embedded Tomcat + `DispatcherServlet` appear).
- **`@SpringBootApplication`** = `@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.
- **Starters**: curated dependency bundles (`spring-boot-starter-web`, `-data-jpa`, `-test`).
- **`application.properties`/`.yml`** externalized config, feeding `@Value`/`@ConfigurationProperties`.
- Embedded server — no external Tomcat/WAR deployment needed; `java -jar app.jar` just runs.

A minimal REST controller (conceptual — needs `spring-boot-starter-web` to run):
```java
@RestController
@RequestMapping("/users")
class UserController {
    private final UserService service;
    UserController(UserService service) { this.service = service; }   // still constructor injection

    @GetMapping("/{id}")
    ResponseEntity<User> get(@PathVariable Long id) {
        return service.find(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
    }
}
```

## Interview one-liners

- **What problem does DI solve?** Decouples object construction from object use — enables
  testability, swappable implementations, and centralizes configuration.
- **`@Component` vs `@Bean`?** `@Component` is a class-level annotation for classes you
  own (scanned automatically). `@Bean` is a method-level annotation inside a
  `@Configuration` class, typically for third-party classes you can't annotate.
- **What happens if two beans match one `@Autowired` field?** `NoUniqueBeanDefinitionException`
  unless resolved via `@Primary` or `@Qualifier`.
- **Why constructor injection?** Immutability (`final` fields), explicit required
  dependencies, fails fast at startup, testable without a container.
- **Singleton bean vs GoF Singleton pattern (module 11)?** Different scopes: a Spring
  singleton is one-per-**container**, not one-per-**JVM** like the GoF pattern.

## Run

```bash
mvn test
mvn compile exec:java
```
