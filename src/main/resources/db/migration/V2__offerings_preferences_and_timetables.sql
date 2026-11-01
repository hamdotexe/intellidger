-- Everything after the first slice: blocks become hard and soft kinds, a teaching load keeps only its hard
-- maximums (what a teacher would like is a preference now), and rooms, sections, courses, offerings, preferences
-- and timetables arrive. No data was ever entered through V1 (there is no screen for it yet), so the old teacher
-- blocks are dropped rather than carried over.

-- Teaching loads: the hard maximums stay, named for what they count; the preferred ranges became teacher
-- preferences.
alter table teaching_loads drop column preferred_min_per_day;
alter table teaching_loads drop column preferred_max_per_day;
alter table teaching_loads drop column preferred_min_per_week;
alter table teaching_loads drop column preferred_max_per_week;
alter table teaching_loads_aud drop column preferred_min_per_day;
alter table teaching_loads_aud drop column preferred_max_per_day;
alter table teaching_loads_aud drop column preferred_min_per_week;
alter table teaching_loads_aud drop column preferred_max_per_week;
alter table teaching_loads rename column max_per_day to max_periods_per_day;
alter table teaching_loads rename column max_per_week to max_periods_per_week;
alter table teaching_loads_aud rename column max_per_day to max_periods_per_day;
alter table teaching_loads_aud rename column max_per_week to max_periods_per_week;

drop table teacher_blocks_aud;
drop table teacher_blocks;

create table rooms (
    id                   char(36)     not null primary key,
    jpa_version          bigint       not null,
    name                 varchar(255) not null,
    max_sections_at_once integer      not null,
    constraint uk_rooms_name unique (name)
);

create table rooms_aud (
    id                   char(36) not null,
    rev                  integer  not null references revision_info (rev),
    revtype              tinyint,
    name                 varchar(255),
    max_sections_at_once integer,
    primary key (id, rev)
);

create table sections (
    id          char(36)     not null primary key,
    jpa_version bigint       not null,
    name        varchar(255) not null,
    constraint uk_sections_name unique (name)
);

create table sections_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    name    varchar(255),
    primary key (id, rev)
);

create table courses (
    id          char(36)     not null primary key,
    jpa_version bigint       not null,
    name        varchar(255) not null,
    constraint uk_courses_name unique (name)
);

create table courses_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    name    varchar(255),
    primary key (id, rev)
);

-- teachers' blocks: one per teacher and period, hard or soft
create table teacher_blocks (
    id          char(36)    not null primary key,
    jpa_version bigint      not null,
    block_kind  varchar(31) not null,
    teacher_id  char(36)    not null references teachers (id),
    period_id   char(36)    not null references periods (id),
    constraint uk_teacher_blocks_teacher_period unique (teacher_id, period_id)
);

create table teacher_blocks_aud (
    id          char(36)    not null,
    rev     integer  not null references revision_info (rev),
    revtype     tinyint,
    block_kind  varchar(31) not null,
    teacher_id  char(36),
    period_id   char(36),
    primary key (id, rev)
);

create table teacher_hard_blocks (
    id char(36) not null primary key references teacher_blocks (id)
);

create table teacher_hard_blocks_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table teacher_soft_blocks (
    id     char(36) not null primary key references teacher_blocks (id),
    weight integer  not null
);

create table teacher_soft_blocks_aud (
    id     char(36) not null,
    rev    integer  not null references revision_info (rev),
    weight integer,
    primary key (id, rev)
);

-- rooms' blocks: one per room and period, hard or soft
create table room_blocks (
    id          char(36)    not null primary key,
    jpa_version bigint      not null,
    block_kind  varchar(31) not null,
    room_id     char(36)    not null references rooms (id),
    period_id   char(36)    not null references periods (id),
    constraint uk_room_blocks_room_period unique (room_id, period_id)
);

create table room_blocks_aud (
    id          char(36)    not null,
    rev     integer  not null references revision_info (rev),
    revtype     tinyint,
    block_kind  varchar(31) not null,
    room_id     char(36),
    period_id   char(36),
    primary key (id, rev)
);

create table room_hard_blocks (
    id char(36) not null primary key references room_blocks (id)
);

create table room_hard_blocks_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table room_soft_blocks (
    id     char(36) not null primary key references room_blocks (id),
    weight integer  not null
);

create table room_soft_blocks_aud (
    id     char(36) not null,
    rev    integer  not null references revision_info (rev),
    weight integer,
    primary key (id, rev)
);

-- sections' blocks: one per section and period, hard or soft
create table section_blocks (
    id          char(36)    not null primary key,
    jpa_version bigint      not null,
    block_kind  varchar(31) not null,
    section_id  char(36)    not null references sections (id),
    period_id   char(36)    not null references periods (id),
    constraint uk_section_blocks_section_period unique (section_id, period_id)
);

create table section_blocks_aud (
    id          char(36)    not null,
    rev     integer  not null references revision_info (rev),
    revtype     tinyint,
    block_kind  varchar(31) not null,
    section_id  char(36),
    period_id   char(36),
    primary key (id, rev)
);

create table section_hard_blocks (
    id char(36) not null primary key references section_blocks (id)
);

create table section_hard_blocks_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table section_soft_blocks (
    id     char(36) not null primary key references section_blocks (id),
    weight integer  not null
);

create table section_soft_blocks_aud (
    id     char(36) not null,
    rev    integer  not null references revision_info (rev),
    weight integer,
    primary key (id, rev)
);

-- Offerings: a course for a section in a term, in a theory part, a lab part or both
create table offerings (
    id          char(36) not null primary key,
    jpa_version bigint   not null,
    term_id     char(36) not null references terms (id),
    course_id   char(36) not null references courses (id),
    section_id  char(36) not null references sections (id),
    constraint uk_offerings_term_course_section unique (term_id, course_id, section_id)
);

create table offerings_aud (
    id         char(36) not null,
    rev        integer  not null references revision_info (rev),
    revtype    tinyint,
    term_id    char(36),
    course_id  char(36),
    section_id char(36),
    primary key (id, rev)
);

create table offering_parts (
    id                char(36)    not null primary key,
    jpa_version       bigint      not null,
    part_kind         varchar(31) not null,
    offering_id       char(36)    not null references offerings (id),
    only_listed_rooms boolean     not null,
    constraint uk_offering_parts_offering_kind unique (offering_id, part_kind)
);

create table offering_parts_aud (
    id                char(36)    not null,
    rev               integer     not null references revision_info (rev),
    revtype           tinyint,
    part_kind         varchar(31) not null,
    offering_id       char(36),
    only_listed_rooms boolean,
    primary key (id, rev)
);

create table offered_theories (
    id char(36) not null primary key references offering_parts (id)
);

create table offered_theories_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table offered_labs (
    id char(36) not null primary key references offering_parts (id)
);

create table offered_labs_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

-- A part's session lengths, in the order entered
create table offering_part_sessions (
    part_id  char(36) not null references offering_parts (id),
    position integer  not null,
    length_in_periods integer not null,
    primary key (part_id, position)
);

create table offering_part_sessions_aud (
    part_id  char(36) not null,
    position integer  not null,
    length_in_periods integer not null,
    rev      integer  not null references revision_info (rev),
    revtype  tinyint,
    primary key (part_id, position, length_in_periods, rev)
);

create table offering_part_instructors (
    part_id    char(36) not null references offering_parts (id),
    position   integer  not null,
    teacher_id char(36) not null references teachers (id),
    priority   integer  not null,
    primary key (part_id, position)
);

create table offering_part_instructors_aud (
    part_id    char(36) not null,
    position   integer  not null,
    rev        integer  not null references revision_info (rev),
    revtype    tinyint  not null,
    teacher_id char(36),
    priority   integer,
    primary key (part_id, position, rev, revtype)
);

create table offering_part_rooms (
    part_id  char(36) not null references offering_parts (id),
    position integer  not null,
    room_id  char(36) not null references rooms (id),
    priority integer  not null,
    primary key (part_id, position)
);

create table offering_part_rooms_aud (
    part_id  char(36) not null,
    position integer  not null,
    rev      integer  not null references revision_info (rev),
    revtype  tinyint  not null,
    room_id  char(36),
    priority integer,
    primary key (part_id, position, rev, revtype)
);

create table offering_part_impossible_rooms (
    part_id char(36) not null references offering_parts (id),
    room_id char(36) not null references rooms (id),
    primary key (part_id, room_id)
);

create table offering_part_impossible_rooms_aud (
    part_id char(36) not null,
    room_id char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    primary key (part_id, room_id, rev)
);

create table offered_lab_engineers (
    lab_id     char(36) not null references offered_labs (id),
    position   integer  not null,
    teacher_id char(36) not null references teachers (id),
    priority   integer  not null,
    primary key (lab_id, position)
);

create table offered_lab_engineers_aud (
    lab_id     char(36) not null,
    position   integer  not null,
    rev        integer  not null references revision_info (rev),
    revtype    tinyint  not null,
    teacher_id char(36),
    priority   integer,
    primary key (lab_id, position, rev, revtype)
);

-- Preferences: one row each, its kind in a table of its own
create table section_preferences (
    id              char(36)    not null primary key,
    jpa_version     bigint      not null,
    preference_kind varchar(31) not null,
    term_id         char(36)    not null references terms (id),
    section_id      char(36)    not null references sections (id),
    weight          integer     not null
);

create table section_preferences_aud (
    id              char(36)    not null,
    rev             integer     not null references revision_info (rev),
    revtype         tinyint,
    preference_kind varchar(31) not null,
    term_id         char(36),
    section_id      char(36),
    weight          integer,
    primary key (id, rev)
);

create table section_preferred_instructors (
    id char(36) not null primary key references section_preferences (id)
);

create table section_preferred_instructors_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table section_preferred_rooms (
    id char(36) not null primary key references section_preferences (id)
);

create table section_preferred_rooms_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table section_off_by (
    id char(36) not null primary key references section_preferences (id),
    time time not null
);

create table section_off_by_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    time time,
    primary key (id, rev)
);

create table section_off_by_days (
    preference_id char(36)     not null references section_off_by (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table section_off_by_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table section_not_before (
    id char(36) not null primary key references section_preferences (id),
    time time not null
);

create table section_not_before_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    time time,
    primary key (id, rev)
);

create table section_not_before_days (
    preference_id char(36)     not null references section_not_before (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table section_not_before_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table section_daily_loads (
    id char(36) not null primary key references section_preferences (id),
    min_periods integer not null,
    max_periods integer not null
);

create table section_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods integer,
    max_periods integer,
    primary key (id, rev)
);

create table section_daily_load_days (
    preference_id char(36)     not null references section_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table section_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table section_free_days (
    id char(36) not null primary key references section_preferences (id),
    how_many_days integer not null
);

create table section_free_days_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    how_many_days integer,
    primary key (id, rev)
);

create table section_days_off (
    id char(36) not null primary key references section_preferences (id),
    day varchar(255) not null
);

create table section_days_off_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    day varchar(255),
    primary key (id, rev)
);

create table section_even_days (
    id char(36) not null primary key references section_preferences (id)
);

create table section_even_days_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table section_no_idle_periods (
    id char(36) not null primary key references section_preferences (id)
);

create table section_no_idle_periods_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table section_periods_in_a_row (
    id char(36) not null primary key references section_preferences (id),
    max_periods integer not null
);

create table section_periods_in_a_row_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    max_periods integer,
    primary key (id, rev)
);

create table section_course_spreads (
    id char(36) not null primary key references section_preferences (id),
    min_days_between integer not null
);

create table section_course_spreads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_days_between integer,
    primary key (id, rev)
);

create table section_same_rooms (
    id char(36) not null primary key references section_preferences (id)
);

create table section_same_rooms_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table teacher_preferences (
    id              char(36)    not null primary key,
    jpa_version     bigint      not null,
    preference_kind varchar(31) not null,
    term_id         char(36)    not null references terms (id),
    teacher_id      char(36)    not null references teachers (id),
    weight          integer     not null
);

create table teacher_preferences_aud (
    id              char(36)    not null,
    rev             integer     not null references revision_info (rev),
    revtype         tinyint,
    preference_kind varchar(31) not null,
    term_id         char(36),
    teacher_id      char(36),
    weight          integer,
    primary key (id, rev)
);

create table teacher_off_by (
    id char(36) not null primary key references teacher_preferences (id),
    time time not null
);

create table teacher_off_by_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    time time,
    primary key (id, rev)
);

create table teacher_off_by_days (
    preference_id char(36)     not null references teacher_off_by (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table teacher_off_by_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_not_before (
    id char(36) not null primary key references teacher_preferences (id),
    time time not null
);

create table teacher_not_before_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    time time,
    primary key (id, rev)
);

create table teacher_not_before_days (
    preference_id char(36)     not null references teacher_not_before (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table teacher_not_before_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_daily_loads (
    id char(36) not null primary key references teacher_preferences (id),
    min_periods integer not null,
    max_periods integer not null
);

create table teacher_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods integer,
    max_periods integer,
    primary key (id, rev)
);

create table teacher_daily_load_days (
    preference_id char(36)     not null references teacher_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table teacher_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_weekly_loads (
    id char(36) not null primary key references teacher_preferences (id),
    min_periods integer not null,
    max_periods integer not null
);

create table teacher_weekly_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods integer,
    max_periods integer,
    primary key (id, rev)
);

create table teacher_free_days (
    id char(36) not null primary key references teacher_preferences (id),
    how_many_days integer not null
);

create table teacher_free_days_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    how_many_days integer,
    primary key (id, rev)
);

create table teacher_days_off (
    id char(36) not null primary key references teacher_preferences (id),
    day varchar(255) not null
);

create table teacher_days_off_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    day varchar(255),
    primary key (id, rev)
);

create table teacher_even_days (
    id char(36) not null primary key references teacher_preferences (id)
);

create table teacher_even_days_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table teacher_no_idle_periods (
    id char(36) not null primary key references teacher_preferences (id)
);

create table teacher_no_idle_periods_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table teacher_periods_in_a_row (
    id char(36) not null primary key references teacher_preferences (id),
    max_periods integer not null
);

create table teacher_periods_in_a_row_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    max_periods integer,
    primary key (id, rev)
);

-- Timetables: one draft per term and saved versions, their sessions, and who teaches each part
create table timetables (
    id             char(36)    not null primary key,
    jpa_version    bigint      not null,
    timetable_kind varchar(31) not null,
    term_id        char(36)    not null references terms (id)
);

create table timetables_aud (
    id             char(36)    not null,
    rev            integer     not null references revision_info (rev),
    revtype        tinyint,
    timetable_kind varchar(31) not null,
    term_id        char(36),
    primary key (id, rev)
);

create table timetable_drafts (
    id char(36) not null primary key references timetables (id)
);

create table timetable_drafts_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table timetable_versions (
    id         char(36)     not null primary key references timetables (id),
    label      varchar(255) not null,
    saved_at   timestamp    not null,
    hard_score integer      not null,
    soft_score integer      not null
);

create table timetable_versions_aud (
    id         char(36) not null,
    rev        integer  not null references revision_info (rev),
    label      varchar(255),
    saved_at   timestamp,
    hard_score integer,
    soft_score integer,
    primary key (id, rev)
);

create table sessions (
    id              char(36) not null primary key,
    jpa_version     bigint   not null,
    timetable_id    char(36) not null references timetables (id),
    part_id         char(36) not null references offering_parts (id),
    length_in_periods integer  not null,
    start_period_id char(36) not null references periods (id),
    room_id         char(36) not null references rooms (id),
    locked          boolean  not null
);

create table sessions_aud (
    id              char(36) not null,
    rev             integer  not null references revision_info (rev),
    revtype         tinyint,
    timetable_id    char(36),
    part_id         char(36),
    length_in_periods integer,
    start_period_id char(36),
    room_id         char(36),
    locked          boolean,
    primary key (id, rev)
);

create table instructor_assignments (
    id           char(36) not null primary key,
    jpa_version  bigint   not null,
    timetable_id char(36) not null references timetables (id),
    part_id      char(36) not null references offering_parts (id),
    teacher_id   char(36) not null references teachers (id),
    constraint uk_instructor_assignments_timetable_part unique (timetable_id, part_id)
);

create table instructor_assignments_aud (
    id           char(36) not null,
    rev          integer  not null references revision_info (rev),
    revtype      tinyint,
    timetable_id char(36),
    part_id      char(36),
    teacher_id   char(36),
    primary key (id, rev)
);

create table lab_engineer_assignments (
    id           char(36) not null primary key,
    jpa_version  bigint   not null,
    timetable_id char(36) not null references timetables (id),
    lab_id       char(36) not null references offered_labs (id),
    teacher_id   char(36) not null references teachers (id),
    constraint uk_lab_engineer_assignments_timetable_lab unique (timetable_id, lab_id)
);

create table lab_engineer_assignments_aud (
    id           char(36) not null,
    rev          integer  not null references revision_info (rev),
    revtype      tinyint,
    timetable_id char(36),
    lab_id       char(36),
    teacher_id   char(36),
    primary key (id, rev)
);
