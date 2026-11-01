-- A preference that applies on some days keeps its own value for each day, such as at most 4 periods on Mondays and
-- 6 on Thursdays, or done by 13:00 on Thursdays and 15:00 on Mondays: one row per wish, the day tables holding a day
-- and its value. The single value the whole row shared goes. No preference was ever entered (there is no screen for
-- them yet), so the day tables are dropped and made again rather than carried over.

alter table section_off_by drop column time;
alter table section_off_by_aud drop column time;
drop table section_off_by_days_aud;
drop table section_off_by_days;

create table section_off_by_days (
    preference_id char(36)     not null references section_off_by (id),
    day           varchar(255) not null,
    off_by        time         not null,
    primary key (preference_id, day)
);

create table section_off_by_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    off_by        time         not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, off_by, rev)
);

alter table section_not_before drop column time;
alter table section_not_before_aud drop column time;
drop table section_not_before_days_aud;
drop table section_not_before_days;

create table section_not_before_days (
    preference_id char(36)     not null references section_not_before (id),
    day           varchar(255) not null,
    not_before    time         not null,
    primary key (preference_id, day)
);

create table section_not_before_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    not_before    time         not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, not_before, rev)
);

alter table section_min_daily_loads drop column min_periods_per_day;
alter table section_min_daily_loads_aud drop column min_periods_per_day;
drop table section_min_daily_load_days_aud;
drop table section_min_daily_load_days;

create table section_min_daily_load_days (
    preference_id char(36)     not null references section_min_daily_loads (id),
    day           varchar(255) not null,
    min_periods   integer      not null,
    primary key (preference_id, day)
);

create table section_min_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    min_periods   integer      not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, min_periods, rev)
);

alter table section_max_daily_loads drop column max_periods_per_day;
alter table section_max_daily_loads_aud drop column max_periods_per_day;
drop table section_max_daily_load_days_aud;
drop table section_max_daily_load_days;

create table section_max_daily_load_days (
    preference_id char(36)     not null references section_max_daily_loads (id),
    day           varchar(255) not null,
    max_periods   integer      not null,
    primary key (preference_id, day)
);

create table section_max_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    max_periods   integer      not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, max_periods, rev)
);

alter table teacher_off_by drop column time;
alter table teacher_off_by_aud drop column time;
drop table teacher_off_by_days_aud;
drop table teacher_off_by_days;

create table teacher_off_by_days (
    preference_id char(36)     not null references teacher_off_by (id),
    day           varchar(255) not null,
    off_by        time         not null,
    primary key (preference_id, day)
);

create table teacher_off_by_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    off_by        time         not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, off_by, rev)
);

alter table teacher_not_before drop column time;
alter table teacher_not_before_aud drop column time;
drop table teacher_not_before_days_aud;
drop table teacher_not_before_days;

create table teacher_not_before_days (
    preference_id char(36)     not null references teacher_not_before (id),
    day           varchar(255) not null,
    not_before    time         not null,
    primary key (preference_id, day)
);

create table teacher_not_before_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    not_before    time         not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, not_before, rev)
);

alter table teacher_min_daily_loads drop column min_periods_per_day;
alter table teacher_min_daily_loads_aud drop column min_periods_per_day;
drop table teacher_min_daily_load_days_aud;
drop table teacher_min_daily_load_days;

create table teacher_min_daily_load_days (
    preference_id char(36)     not null references teacher_min_daily_loads (id),
    day           varchar(255) not null,
    min_periods   integer      not null,
    primary key (preference_id, day)
);

create table teacher_min_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    min_periods   integer      not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, min_periods, rev)
);

alter table teacher_max_daily_loads drop column max_periods_per_day;
alter table teacher_max_daily_loads_aud drop column max_periods_per_day;
drop table teacher_max_daily_load_days_aud;
drop table teacher_max_daily_load_days;

create table teacher_max_daily_load_days (
    preference_id char(36)     not null references teacher_max_daily_loads (id),
    day           varchar(255) not null,
    max_periods   integer      not null,
    primary key (preference_id, day)
);

create table teacher_max_daily_load_days_aud (
    preference_id char(36)     not null,
    day           varchar(255) not null,
    max_periods   integer      not null,
    rev           integer      not null references revision_info (rev),
    revtype       tinyint,
    primary key (preference_id, day, max_periods, rev)
);
