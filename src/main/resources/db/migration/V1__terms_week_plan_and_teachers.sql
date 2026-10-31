-- Envers: one row per saved change; every *_aud table points here.
create table revision_info (
    rev      integer primary key autoincrement,
    revtstmp bigint  not null
);

create table terms (
    id          char(36)     not null primary key,
    jpa_version bigint       not null,
    name        varchar(255) not null,
    constraint uk_terms_name unique (name)
);

create table terms_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    name    varchar(255),
    primary key (id, rev)
);

create table periods (
    id          char(36)    not null primary key,
    jpa_version bigint      not null,
    term_id     char(36)    not null references terms (id),
    day         varchar(16) not null,
    number      integer     not null,
    starts_at   time        not null,
    ends_at     time        not null,
    constraint uk_periods_term_day_number unique (term_id, day, number)
);

create table periods_aud (
    id        char(36) not null,
    rev       integer  not null references revision_info (rev),
    revtype   tinyint,
    term_id   char(36),
    day       varchar(16),
    number    integer,
    starts_at time,
    ends_at   time,
    primary key (id, rev)
);

create table teachers (
    id          char(36)     not null primary key,
    jpa_version bigint       not null,
    name        varchar(255) not null,
    constraint uk_teachers_name unique (name)
);

create table teachers_aud (
    id      char(36) not null,
    rev     integer  not null references revision_info (rev),
    revtype tinyint,
    name    varchar(255),
    primary key (id, rev)
);

create table teacher_blocks (
    id          char(36)    not null primary key,
    jpa_version bigint      not null,
    teacher_id  char(36)    not null references teachers (id),
    period_id   char(36)    not null references periods (id),
    strength    varchar(16) not null,
    constraint uk_teacher_blocks_teacher_period unique (teacher_id, period_id)
);

create table teacher_blocks_aud (
    id         char(36) not null,
    rev        integer  not null references revision_info (rev),
    revtype    tinyint,
    teacher_id char(36),
    period_id  char(36),
    strength   varchar(16),
    primary key (id, rev)
);

create table teaching_loads (
    id                     char(36) not null primary key,
    jpa_version            bigint   not null,
    term_id                char(36) not null references terms (id),
    teacher_id             char(36) not null references teachers (id),
    max_per_day            integer  not null,
    preferred_min_per_day  integer  not null,
    preferred_max_per_day  integer  not null,
    max_per_week           integer  not null,
    preferred_min_per_week integer  not null,
    preferred_max_per_week integer  not null,
    constraint uk_teaching_loads_term_teacher unique (term_id, teacher_id)
);

create table teaching_loads_aud (
    id                     char(36) not null,
    rev                    integer  not null references revision_info (rev),
    revtype                tinyint,
    term_id                char(36),
    teacher_id             char(36),
    max_per_day            integer,
    preferred_min_per_day  integer,
    preferred_max_per_day  integer,
    max_per_week           integer,
    preferred_min_per_week integer,
    preferred_max_per_week integer,
    primary key (id, rev)
);
