-- A third term preference, true or false with no default in the app:
--   labs_count_as_instructor_load: whether a lab's periods count toward its instructor's teaching load, as they always
--   do toward its lab engineer's. Keeping a lab's time free and teaching in it are two things; an instructor who
--   doesn't attend labs never has them count.
-- No term was ever entered (there is no screen for terms yet); the default below is only there because SQLite won't
-- add a not-null column without one.
alter table terms add column labs_count_as_instructor_load boolean not null default 0;
alter table terms_aud add column labs_count_as_instructor_load boolean;
