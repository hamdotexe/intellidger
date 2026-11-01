-- A timetable that breaks a hard rule is never saved, so a saved version's hard score was always 0 and told nothing;
-- only its soft score is kept.
alter table timetable_versions drop column hard_score;
alter table timetable_versions_aud drop column hard_score;
