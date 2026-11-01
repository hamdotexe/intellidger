-- A load preference becomes one way: "at least" and "at most" are kinds of their own, each one number with its own
-- weight, rather than one range whose minimum was 0 when none was wanted and whose two ends cost the same. A range
-- is now two rows, and either can be left out. No preference was ever entered (there is no screen for them yet), so
-- the old range tables are dropped rather than carried over.

drop table section_daily_load_days_aud;
drop table section_daily_load_days;
drop table section_daily_loads_aud;
drop table section_daily_loads;
drop table teacher_daily_load_days_aud;
drop table teacher_daily_load_days;
drop table teacher_daily_loads_aud;
drop table teacher_daily_loads;
drop table teacher_weekly_loads_aud;
drop table teacher_weekly_loads;

create table section_min_daily_loads (
    id char(36) not null primary key references section_preferences (id),
    min_periods_per_day integer not null
);

create table section_min_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods_per_day integer,
    primary key (id, rev)
);

create table section_min_daily_load_days (
    preference_id char(36)     not null references section_min_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table section_min_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table section_max_daily_loads (
    id char(36) not null primary key references section_preferences (id),
    max_periods_per_day integer not null
);

create table section_max_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    max_periods_per_day integer,
    primary key (id, rev)
);

create table section_max_daily_load_days (
    preference_id char(36)     not null references section_max_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table section_max_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_min_daily_loads (
    id char(36) not null primary key references teacher_preferences (id),
    min_periods_per_day integer not null
);

create table teacher_min_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods_per_day integer,
    primary key (id, rev)
);

create table teacher_min_daily_load_days (
    preference_id char(36)     not null references teacher_min_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table teacher_min_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_max_daily_loads (
    id char(36) not null primary key references teacher_preferences (id),
    max_periods_per_day integer not null
);

create table teacher_max_daily_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    max_periods_per_day integer,
    primary key (id, rev)
);

create table teacher_max_daily_load_days (
    preference_id char(36)     not null references teacher_max_daily_loads (id),
    day           varchar(255) not null,
    primary key (preference_id, day)
);

create table teacher_max_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, rev)
);

create table teacher_min_weekly_loads (
    id char(36) not null primary key references teacher_preferences (id),
    min_periods_per_week integer not null
);

create table teacher_min_weekly_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    min_periods_per_week integer,
    primary key (id, rev)
);

create table teacher_max_weekly_loads (
    id char(36) not null primary key references teacher_preferences (id),
    max_periods_per_week integer not null
);

create table teacher_max_weekly_loads_aud (
    id  char(36) not null,
    rev integer  not null references revision_info (rev),
    max_periods_per_week integer,
    primary key (id, rev)
);
