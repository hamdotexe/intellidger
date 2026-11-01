-- Intellidger has one user, one database and one connection: nothing ever writes a row behind anyone's back, so
-- rows carry no optimistic-lock version. Envers never audited it, so the audit tables keep their shape.

alter table terms drop column jpa_version;
alter table periods drop column jpa_version;
alter table teachers drop column jpa_version;
alter table teaching_loads drop column jpa_version;
alter table teacher_blocks drop column jpa_version;
alter table rooms drop column jpa_version;
alter table room_blocks drop column jpa_version;
alter table sections drop column jpa_version;
alter table section_blocks drop column jpa_version;
alter table courses drop column jpa_version;
alter table offerings drop column jpa_version;
alter table offering_parts drop column jpa_version;
alter table section_preferences drop column jpa_version;
alter table teacher_preferences drop column jpa_version;
alter table timetables drop column jpa_version;
alter table sessions drop column jpa_version;
alter table instructor_assignments drop column jpa_version;
alter table lab_engineer_assignments drop column jpa_version;
