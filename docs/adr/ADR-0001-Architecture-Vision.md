# ADR-0001: Architecture Vision & Baseline Technology Stack

## Status
**Accepted** (2026-09-01)

## Context & Problem Statement
To demonstrate technical modernization and readiness for a Senior Software Engineer (L7) / Lead Architect role at enterprise organizations (e.g., Mastercard), we require an anchor system that reflects real-world enterprise constraints: high data integrity, idempotent transaction processing, distributed resilience, observability, and event-driven decoupled architecture.

The project will evolve through 5 distinct phases over 4 months (Sept 2026 – Jan 2027), shifting from a modular monolithic REST API to a containerized, instrumented, event-driven payment engine.

## Decision
We establish **`PayPulse-Core`** as the single evolutionary project.

### 1. Technology Choices
* **Language & Runtime:** Java 17 LTS (Enterprise standard baseline, Records, Sealed classes, Pattern matching).
* **Framework:** Spring Boot 3.x (Spring Framework 6, Spring Data JPA, Spring Security).
* **Build System:** Apache Maven (Standard enterprise POM management, predictable plugin lifecycle, zero licensing barriers).
* **Persistence:** PostgreSQL (ACID compliance, row-level locking, JSONB support for payload auditing).
* **Source Control:** Git (Trunk-based development with short-lived feature branches, Conventional Commits).
* **IDE & Tooling:** Antigravity / VS Code with Java Extension Pack and integrated terminal.

### 2. Conceptual Mapping (.NET / C# / TFS to Modern Stack)

| Legacy / C# / TFS Concept | Modern / Java / Git Equivalent | Architectural & Workflow Note |
| :--- | :--- | :--- |
| `TFS Check-out / Check-in` | `git add` (Stage) -> `git commit` (Local snapshot) -> `git push` (Remote sync) | Git separates local staging & snapshotting from remote network synchronization. |
| `TFS Shelveset` | `git stash` or feature branch (`git switch -c feat/...`) | Local, zero-server-dependency temporary work saving. |
| `TFS Server-Side History` | Local DAG (Directed Acyclic Graph) of immutable commits | History is fully local, verifiable, and cryptographic (SHA-1/256). |
| `ASP.NET Core Web API` / `ControllerBase` | `@RestController`, `@RequestMapping` | Spring MVC controller model. |
| `Entity Framework Core` / `DbContext` | `Hibernate` / `JPA` / `EntityManager` | JPA is the specification; Hibernate is the default ORM provider. |
| `DbSet<T>` / LINQ Queries | `JpaRepository<T, ID>` / Spring Data Derived Queries | Spring generates proxy implementations at startup. |
| `appsettings.json` | `application.yml` / `application.properties` | Hierarchical configuration with profile management. |
| `Program.cs` / `WebApplication.CreateBuilder()` | `@SpringBootApplication` / `SpringApplication.run()` | Spring boot application entry point & component scanning. |
| `IServiceCollection` / `AddScoped<T>()` | `@Service`, `@Component`, `@Autowired` (Default: Singleton scope) | Spring beans default to **Singleton**; distinct from .NET's default Scoped controllers. |
| `record` (C# 9+) | `record` (Java 14+) | Immutable data transfer objects with auto-generated getters, `equals`, and `hashCode`. |
| `Task<T>` / `async-await` | `CompletableFuture<T>` / Virtual Threads (Java 21) | Java 17 relies on ThreadPool / reactive or standard blocking I/O with worker pools. |

### 3. Core Enterprise Domain Requirements (Phase 1)
* **Idempotency Engine:** Prevent double-charging via unique client idempotency keys stored and locked at the database level.
* **Double-Entry Ledger:** Every transaction must have balanced debit and credit entries (sum = 0).
* **Explicit Transaction Boundaries:** Deep understanding of `@Transactional` isolation, rollback rules, and proxy behavior.

## Consequences & Trade-offs
* **Pros:**
  * Highly relevant for enterprise fintech and payments architecture.
  * Direct 1-to-1 mapping from existing C# / EF Core foundation accelerates learning.
  * Provides concrete evidence of architecture decision-making and performance tuning.
* **Cons / Watch-outs:**
  * Spring Bean lifecycle defaults to Singleton, which requires strict immutability and thread safety compared to transient/scoped .NET services.
  * Hibernate first-level cache and dirty checking differ from EF Core change tracker nuances (e.g., lazy loading exceptions, N+1 query problem).
