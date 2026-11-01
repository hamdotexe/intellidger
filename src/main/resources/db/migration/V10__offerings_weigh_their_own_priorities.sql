-- How much an offering's instructor, room and lab engineer priorities matter becomes the offering's own: a weight for
-- each list, what a timetable pays for each step below the first priority. The section preferences that carried it
-- (preferred instructors, preferred rooms) go. No offering or preference was ever entered (there is no screen for
-- them yet); the defaults below are only there because SQLite won't add a not-null column without one.
drop table section_preferred_instructors_aud;
drop table section_preferred_instructors;
drop table section_preferred_rooms_aud;
drop table section_preferred_rooms;

alter table offerings add column instructor_preference_weight integer not null default 0;
alter table offerings add column room_preference_weight integer not null default 0;
alter table offerings_aud add column instructor_preference_weight integer;
alter table offerings_aud add column room_preference_weight integer;
alter table lab_offerings add column lab_engineer_preference_weight integer not null default 0;
alter table lab_offerings_aud add column lab_engineer_preference_weight integer;
