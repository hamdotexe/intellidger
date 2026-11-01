-- A course's theory and its lab become offerings of their own, rather than parts of one offering: AI for BESE-30-C
-- is a theory offering and a lab offering, each with its sessions, instructors and rooms. Sessions and assignments
-- point straight at an offering. No offering, session or assignment was ever entered (there is no screen for them
-- yet), so their tables are dropped and made again rather than carried over.

drop table lab_engineer_assignments_aud;
drop table lab_engineer_assignments;
drop table instructor_assignments_aud;
drop table instructor_assignments;
drop table sessions_aud;
drop table sessions;
drop table offered_lab_engineers_aud;
drop table offered_lab_engineers;
drop table offering_part_impossible_rooms_aud;
drop table offering_part_impossible_rooms;
drop table offering_part_rooms_aud;
drop table offering_part_rooms;
drop table offering_part_instructors_aud;
drop table offering_part_instructors;
drop table offering_part_sessions_aud;
drop table offering_part_sessions;
drop table offered_labs_aud;
drop table offered_labs;
drop table offered_theories_aud;
drop table offered_theories;
drop table offering_parts_aud;
drop table offering_parts;
drop table offerings_aud;
drop table offerings;

create table offerings (
    id                char(36)    not null primary key,
    offering_kind     varchar(31) not null,
    term_id           char(36)    not null references terms (id),
    course_id         char(36)    not null references courses (id),
    section_id        char(36)    not null references sections (id),
    only_listed_rooms boolean     not null,
    constraint uk_offerings_term_course_section_kind unique (term_id, course_id, section_id, offering_kind)
);

create table offerings_aud (
    id                char(36)    not null,
    rev               integer     not null references revision_info (rev),
    revtype           tinyint,
    offering_kind     varchar(31) not null,
    term_id           char(36),
    course_id         char(36),
    section_id        char(36),
    only_listed_rooms boolean,
    primary key (id, rev)
);

create table theory_offerings (
    id char(36) not null primary key references offerings (id)
);

create table theory_offerings_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

create table lab_offerings (
    id char(36) not null primary key references offerings (id)
);

create table lab_offerings_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    primary key (id, rev)
);

-- An offering's session lengths, in the order entered
create table offering_session_lengths (
    offering_id       char(36) not null references offerings (id),
    position          integer  not null,
    length_in_periods integer  not null,
    primary key (offering_id, position)
);

create table offering_session_lengths_aud (
    offering_id       char(36) not null,
    position          integer  not null,
    length_in_periods integer  not null,
    rev               integer  not null references revision_info (rev),
    revtype           tinyint,
    primary key (offering_id, position, length_in_periods, rev)
);

create table offering_instructors (
    offering_id char(36) not null references offerings (id),
    position    integer  not null,
    teacher_id  char(36) not null references teachers (id),
    priority    integer  not null,
    primary key (offering_id, position)
);

create table offering_instructors_aud (
    offering_id char(36) not null,
    position    integer  not null,
    rev         integer  not null references revision_info (rev),
    revtype     tinyint  not null,
    teacher_id  char(36),
    priority    integer,
    primary key (offering_id, position, rev, revtype)
);

create table offering_rooms (
    offering_id char(36) not null references offerings (id),
    position    integer  not null,
    room_id     char(36) not null references rooms (id),
    priority    integer  not null,
    primary key (offering_id, position)
);

create table offering_rooms_aud (
    offering_id char(36) not null,
    position    integer  not null,
    rev         integer  not null references revision_info (rev),
    revtype     tinyint  not null,
    room_id     char(36),
    priority    integer,
    primary key (offering_id, position, rev, revtype)
);

create table offering_impossible_rooms (
    offering_id char(36) not null references offerings (id),
    room_id     char(36) not null references rooms (id),
    primary key (offering_id, room_id)
);

create table offering_impossible_rooms_aud (
    offering_id char(36) not null,
    room_id     char(36) not null,
    rev         integer  not null references revision_info (rev),
    revtype     tinyint,
    primary key (offering_id, room_id, rev)
);

create table lab_offering_engineers (
    offering_id char(36) not null references lab_offerings (id),
    position    integer  not null,
    teacher_id  char(36) not null references teachers (id),
    priority    integer  not null,
    primary key (offering_id, position)
);

create table lab_offering_engineers_aud (
    offering_id char(36) not null,
    position    integer  not null,
    rev         integer  not null references revision_info (rev),
    revtype     tinyint  not null,
    teacher_id  char(36),
    priority    integer,
    primary key (offering_id, position, rev, revtype)
);

create table sessions (
    id                char(36) not null primary key,
    timetable_id      char(36) not null references timetables (id),
    offering_id       char(36) not null references offerings (id),
    length_in_periods integer  not null,
    start_period_id   char(36) not null references periods (id),
    room_id           char(36) not null references rooms (id),
    locked            boolean  not null
);

create table sessions_aud (
    id                char(36) not null,
    rev               integer  not null references revision_info (rev),
    revtype           tinyint,
    timetable_id      char(36),
    offering_id       char(36),
    length_in_periods integer,
    start_period_id   char(36),
    room_id           char(36),
    locked            boolean,
    primary key (id, rev)
);

create table instructor_assignments (
    id           char(36) not null primary key,
    timetable_id char(36) not null references timetables (id),
    offering_id  char(36) not null references offerings (id),
    teacher_id   char(36) not null references teachers (id),
    constraint uk_instructor_assignments_timetable_offering unique (timetable_id, offering_id)
);

create table instructor_assignments_aud (
    id           char(36) not null,
    rev          integer  not null references revision_info (rev),
    revtype      tinyint,
    timetable_id char(36),
    offering_id  char(36),
    teacher_id   char(36),
    primary key (id, rev)
);

create table lab_engineer_assignments (
    id           char(36) not null primary key,
    timetable_id char(36) not null references timetables (id),
    offering_id  char(36) not null references lab_offerings (id),
    teacher_id   char(36) not null references teachers (id),
    constraint uk_lab_engineer_assignments_timetable_offering unique (timetable_id, offering_id)
);

create table lab_engineer_assignments_aud (
    id           char(36) not null,
    rev          integer  not null references revision_info (rev),
    revtype      tinyint,
    timetable_id char(36),
    offering_id  char(36),
    teacher_id   char(36),
    primary key (id, rev)
);
