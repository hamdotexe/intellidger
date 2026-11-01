-- Term preferences: rules a term sets for itself, each true or false, with no default in the app.
--   instructors_attend_labs: whether a lab's instructor must be free for its whole session, beside its lab engineer.
--   electives_mutually_exclusive: whether each student takes just one of their batch's electives, so that sections
--   of different electives may meet at the same time.
-- No term was ever entered (there is no screen for terms yet); the defaults below are only there because SQLite
-- won't add a not-null column without one.
alter table terms add column instructors_attend_labs boolean not null default 0;
alter table terms add column electives_mutually_exclusive boolean not null default 0;
alter table terms_aud add column instructors_attend_labs boolean;
alter table terms_aud add column electives_mutually_exclusive boolean;
