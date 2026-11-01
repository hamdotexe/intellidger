# CLAUDE.md

Guidance for Claude Code when working in this repository.

Intellidger is a Windows desktop app that generates and optimizes a university department's weekly timetable. It has
a JavaFX front over Spring (no web server), Hibernate on a local SQLite file, and Timefold for solving. It is a group
project for Software Construction (BESE-30, MCS NUST).

## Timetabling rules: settled, never ask again

- **Consecutive means next to each other in the day.** Periods are consecutive whatever break lies between them:
  lunch, Zuhr prayer, or a 5–15 minute gap. **Only a free period breaks a run.** This holds for a session's own
  periods and for every rule or preference that counts periods in a row.
- **Session lengths are fixed per offering, exactly as entered.** A 2+1 stays 2+1, never 1+1+1 and never 3. A 2
  stays 2, never 1+1. A lab's 3 stays one session of 3 periods in a row. The solver places sessions; it never
  re-splits or merges them.
- **Instructors:** one per offering (a course's theory or its lab for a section), chosen from its priority list.
  Equal priority means equally possible, a single entry means fixed, and an unlisted teacher is impossible. Each
  step below the first priority costs the offering's own instructor preference weight. Lab engineers are teachers,
  the same in every way, with the lab offering's lab engineer preference weight.
- **Labs:** a lab has an instructor and a lab engineer ("Dr Rabia Khan / LE Shehla Farooq"). Its sessions need the
  lab engineer free for all of them; the instructor too only when the term says instructors attend labs. A lab's
  periods always count toward its lab engineer's teaching load, and toward its instructor's only when the term says
  labs count as instructor load, which it can say only while instructors attend labs: being kept free for a lab and
  teaching in it are two different things.
- **Batches and electives:** a batch (BESE-30) has regular sections (BESE-30-A, -B, -C). An elective is offered to
  the whole batch, never to some of its sections: it runs as one or more elective sections in a term (IoT-1, IoT-2,
  Quantum-1), each drawing students from every regular section of the batch. While an elective section meets, none
  of its batch's regular sections can. Sections of the same elective may meet at the same time; sections of
  different electives only when the term says electives are mutually exclusive (each student takes just one).
- **Term preferences** are rules a term sets for itself, each a required true or false with no default: whether
  instructors attend labs, whether labs count as instructor load (refused as true while instructors don't attend
  labs), and whether electives are mutually exclusive. Unlike section and teacher preferences, they carry no weight.
- **Rooms:**
  - Any room by default.
  - Listed rooms are preferred by priority: each step below the first costs the offering's room preference weight,
    for each session, an unlisted room standing one below the last listed. Impossible rooms are never used.
  - "Only these rooms" makes every unlisted room impossible.
  - A room takes one section at a time; the few set to take more (2 or 3) take up to that many in the same period.
    Student numbers are not kept: not for sections, not for rooms.
- **Courses per day:** a section never has the same course twice in one day, theory and lab included.
- **Completeness:** every section gets all its offerings scheduled.
- **No timetable that breaks a hard rule.** If generating or optimizing can't place every session of every section
  without breaking a hard rule, the run has failed: it produces no timetable, the draft stays as it was, nothing is
  saved, and the user is told which hard rules couldn't be kept. A best attempt that breaks one is never kept.
- **Blocks:** a hard block is never used. A soft block may be used, at the cost of its own weight; each soft block
  carries one.
- **Preferences:** each preference is a row with an owner (a section or a teacher), a term and its own weight, and each
  kind is its own class. A timetable's score is separate: output stored with a saved timetable, never an input. There
  are no global weights.
- **A soft rule's weight lives where the rule does,** named as a preference: a soft block's on the block, an
  offering's priority weights on the offering, a wish's on its preference row. Never a separate row that, left out,
  would silently switch the rule off.
- **Contradicting preferences are refused when saved,** by the services, whichever side is saved second: kinds that
  never stand together (even days with free days, a day off or daily loads), values that can't both hold (a minimum
  above a maximum, a minimum on a day off, a teacher's minimum above their teaching load), and a second row of a kind
  that takes one. The full lists are in the `package-info` of `entities/preferences/sections` and `/teachers`.
  Everything else stands together, and the weights decide.
- **Not now:** ignore STI, and assume no combined classes.

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

**No base entity.** Every entity declares its own id, not inherited from a shared superclass:

```java
@Id
@GeneratedValue(strategy = GenerationType.UUID)
@Setter(AccessLevel.NONE)
private UUID id;
```

Entities use Lombok `@Getter @Setter` on the class, so `@Setter(AccessLevel.NONE)` keeps the id read-only.

The only exception is one JPA forces: in an `@Inheritance(strategy = JOINED)` hierarchy, the subclasses get the id
from the root entity, which declares it. A `@MappedSuperclass` never carries it; each entity that extends it does.

**No `@Version`.** Intellidger has one user, one database and one connection: no row is ever written behind anyone's
back, so there is nothing for an optimistic-lock version to guard.

**Numbers are `Integer` with `@NotNull`, never `int`.** An `int` field starts at 0 by itself, so a value nobody set
would save as 0 without a word; an `Integer` starts at null and `@NotNull` refuses it. A default, when there is one,
is written out. Each number's name also says what it counts: `maxPeriodsPerDay`, never `maxPerDay`.

- The schema belongs to Flyway (`src/main/resources/db/migration`), and Hibernate only validates against it
  (`ddl-auto=validate`). Every entity change needs a migration. Never edit a committed migration; add the next `V`.
- **Getting the exact tables Hibernate expects** (Envers audit tables included) when writing a migration: run
  `./mvnw -q test -Dtest=FoundationTest -Dspring.flyway.enabled=false -Dspring.jpa.hibernate.ddl-auto=none
  -Dspring.jpa.properties.jakarta.persistence.schema-generation.scripts.action=create
  -Dspring.jpa.properties.jakarta.persistence.schema-generation.scripts.create-target=<file>`. The SQLite dialect
  leaves foreign keys out, so the migration declares them inline.
- **An audited `@ElementCollection` of embeddables needs an `@OrderColumn`.** Without one, Envers keys its audit rows
  by owner and revision only, and two elements saved together collide.
- **A value for each day of the week** (a time, a number of periods) is an `@ElementCollection` map keyed by
  `DayOfWeek`, stored by name in a `<kind>_days` table with the day's value beside it; a day left out has none. Never
  pack days into one column with a converter.
- SQLite notes: UUIDs are stored as 36-character text, foreign keys are on, and the app uses one connection because
  SQLite allows one writer at a time.
