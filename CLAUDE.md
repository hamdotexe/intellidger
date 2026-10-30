# CLAUDE.md

Guidance for Claude Code when working in this repository.

Intellidger is a Windows desktop app that generates and optimizes a university department's weekly timetable. It has
a JavaFX front over Spring (no web server), Hibernate on a local SQLite file, and Timefold for solving. It is a group
project for Software Construction (BESE-30, MCS NUST).

## Build and run

- **Java 25 is required.** The `java` on PATH may be 17, so set
  `export JAVA_HOME="/c/Program Files/Java/jdk-25"` before running `./mvnw`.
- `./mvnw -q test` compiles and runs `FoundationTest`. That test runs Flyway and Hibernate against a fresh SQLite
  file in a temp folder, so entity/migration drift fails here. It opens no window.
- `./mvnw spring-boot:run` opens the app. Data lives in `%LOCALAPPDATA%\Intellidger\intellidger.db`, logs in
  `%LOCALAPPDATA%\Intellidger\logs\`.

## Layout

- `ui/`: the JavaFX front. `DesktopApp` starts Spring, then the window. Each screen is an FXML controller (a
  prototype-scoped Spring bean) with its FXML in resources at the same package path. Screens call services, never
  repositories.
- `services/`, `repositories/`, `entities/`: by area. Each package's `package-info` says what it holds.
- `shared/persistence/`: `AppDataFolder`, where the database lives.

## Entities

**No base entity.** Every entity declares its own id and optimistic-lock version, not inherited from a shared
superclass:

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
private UUID id;

/** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
@Version
@Column(name = "jpa_version", nullable = false)
private Long jpaVersion;
```

The only exception is one JPA forces: in an `@Inheritance(strategy = JOINED)` hierarchy, the subclasses get the id
and version from the root entity, which declares them. A `@MappedSuperclass` never carries them; each entity that
extends it does.

- The schema belongs to Flyway (`src/main/resources/db/migration`), and Hibernate only validates against it
  (`ddl-auto=validate`). Every entity change needs a migration.
- SQLite notes: UUIDs are stored as 36-character text, foreign keys are on, and the app uses one connection because
  SQLite allows one writer at a time.
