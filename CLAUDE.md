# CLAUDE.md

Guidance for Claude Code when working in this repository.

Intellidger is a Windows desktop app that generates and optimizes a university department's weekly timetable. It has
a JavaFX front over Spring (no web server), Hibernate on a local SQLite file, and Timefold for solving. It is a group
project for Software Construction (BESE-30, MCS NUST).

## Timetabling rules: settled, never ask again

- **Consecutive means next to each other in the day.** Periods are consecutive whatever break lies between them:
  lunch, Zuhr prayer, or a 5–15 minute gap. **Only a free period breaks a run.** This holds for a session's own
  periods and for every rule or preference that counts periods in a row.
- **Session lengths are fixed per offering part, exactly as entered.** A 2+1 stays 2+1, never 1+1+1 and never 3. A 2
  stays 2, never 1+1. A lab's 3 stays one session of 3 periods in a row. The solver places sessions; it never
  re-splits or merges them.
- **Instructors:** one per offering part per section, chosen from the part's priority list. Equal priority means
  equally possible, a single entry means fixed, and an unlisted teacher is impossible. Lab engineers are teachers, the
  same in every way.
- **Rooms:**
  - Any room by default.
  - Listed rooms are preferred by priority, and impossible rooms are never used.
  - "Only these rooms" makes every unlisted room impossible.
  - The room must hold the section.
- **Courses per day:** a section never has the same course twice in one day, theory and lab included.
- **Completeness:** every section gets all its offerings scheduled.
- **Blocks:** a hard block is never used. A soft block may be used, at the cost of its own weight; each soft block
  carries one.
- **Preferences:** each preference is a row with an owner (a section or a teacher), a term and its own weight, and each
  kind is its own class. A timetable's score is separate: output stored with a saved timetable, never an input. There
  are no global weights.
- **Not now:** ignore STI, assume no combined classes, and leave electives for later (they'll become an offering
  subtype).

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
- `shared/audit/`: `RevisionInfo`, Envers' revision table. Entities are `@Audited`, and each audited table gets a
  `*_aud` twin in the same migration.

## Entities

**No base entity.** Every entity declares its own id and optimistic-lock version, not inherited from a shared
superclass:

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Setter(AccessLevel.NONE)
private UUID id;

/** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
@Version
@Column(name = "jpa_version", nullable = false)
@Setter(AccessLevel.NONE)
private Long jpaVersion;
```

Entities use Lombok `@Getter @Setter` on the class, so `@Setter(AccessLevel.NONE)` keeps these two read-only.

The only exception is one JPA forces: in an `@Inheritance(strategy = JOINED)` hierarchy, the subclasses get the id
and version from the root entity, which declares them. A `@MappedSuperclass` never carries them; each entity that
extends it does.

- The schema belongs to Flyway (`src/main/resources/db/migration`), and Hibernate only validates against it
  (`ddl-auto=validate`). Every entity change needs a migration. Never edit a committed migration; add the next `V`.
- **Getting the exact tables Hibernate expects** (Envers audit tables included) when writing a migration: run
  `./mvnw -q test -Dtest=FoundationTest -Dspring.flyway.enabled=false -Dspring.jpa.hibernate.ddl-auto=none
  -Dspring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
  -Dspring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=<file>`. The SQLite dialect
  leaves foreign keys out, so the migration declares them inline.
- **An audited `@ElementCollection` of embeddables needs an `@OrderColumn`.** Without one, Envers keys its audit rows
  by owner and revision only, and two elements saved together collide.
- **Days of the week** that a row applies on go in one column through `DaysOfWeekConverter` ("MONDAY,THURSDAY").
- SQLite notes: UUIDs are stored as 36-character text, foreign keys are on, and the app uses one connection because
  SQLite allows one writer at a time.
