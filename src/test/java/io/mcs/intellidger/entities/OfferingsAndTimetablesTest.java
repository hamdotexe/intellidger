package io.mcs.intellidger.entities;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.entities.courses.Course;
import io.mcs.intellidger.entities.offerings.InstructorPriority;
import io.mcs.intellidger.entities.offerings.OfferedLab;
import io.mcs.intellidger.entities.offerings.OfferedTheory;
import io.mcs.intellidger.entities.offerings.Offering;
import io.mcs.intellidger.entities.offerings.OfferingPart;
import io.mcs.intellidger.entities.offerings.RoomPriority;
import io.mcs.intellidger.entities.rooms.Room;
import io.mcs.intellidger.entities.sections.Section;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.terms.Term;
import io.mcs.intellidger.entities.timetables.InstructorAssignment;
import io.mcs.intellidger.entities.timetables.LabEngineerAssignment;
import io.mcs.intellidger.entities.timetables.Session;
import io.mcs.intellidger.entities.timetables.Timetable;
import io.mcs.intellidger.entities.timetables.TimetableDraft;
import io.mcs.intellidger.entities.timetables.TimetableVersion;
import io.mcs.intellidger.entities.weekplan.Period;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.envers.AuditReaderFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Artificial Intelligence for BESE-30-C, as the department taught it in Fall 2026: a 2+1 theory open to two equal
 * instructors, and a lab of 3 only in the Programming Lab with its lab engineer. It goes into SQLite and comes back as
 * entered, its lists in order and audited; and a timetable placing it, draft or saved, does too.
 */
class OfferingsAndTimetablesTest extends DatabaseTestBase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Test
    void anOfferingWithATheoryAndALabComesBackAsEntered() {
        UUID offeringId = transaction.execute(status -> offering("Fall 2026").getId());

        transaction.executeWithoutResult(status -> {
            Offering offering = entityManager.find(Offering.class, offeringId);
            assertThat(offering.getCourse().getName()).startsWith("Artificial Intelligence");
            assertThat(offering.getSection().getName()).startsWith("BESE-30-C");
            assertThat(offering.getParts()).hasSize(2);

            OfferedTheory theory = part(offering, OfferedTheory.class);
            assertThat(theory.getSessionLengthsInPeriods()).containsExactly(2, 1);
            assertThat(theory.getInstructors()).hasSize(2);
            assertThat(theory.getInstructors().get(0).getTeacher().getName()).startsWith("Lt Col Khawir");
            assertThat(theory.getInstructors().get(1).getPriority()).isEqualTo(1);
            assertThat(theory.isOnlyListedRooms()).isFalse();
            assertThat(theory.getImpossibleRooms()).hasSize(1);
            assertThat(theory.getImpossibleRooms().iterator().next().getName()).startsWith("CR-35");

            OfferedLab lab = part(offering, OfferedLab.class);
            assertThat(lab.getSessionLengthsInPeriods()).containsExactly(3);
            assertThat(lab.getInstructors().get(0).getTeacher().getName()).startsWith("Dr Rabia Khan");
            assertThat(lab.getLabEngineers().get(0).getTeacher().getName()).startsWith("LE Shehla Farooq");
            assertThat(lab.isOnlyListedRooms()).isTrue();
            assertThat(lab.getRooms().get(0).getRoom().getName()).startsWith("Programming Lab");
            assertThat(lab.getRooms().get(0).getRoom().getMaxSectionsAtOnce()).isEqualTo(1);

            assertThat(AuditReaderFactory.get(entityManager).getRevisions(OfferedTheory.class, theory.getId()))
                    .hasSize(1);
        });
    }

    @Test
    void onlyTheseRoomsWithNoRoomListedIsRefused() {
        assertThatThrownBy(() -> transaction.executeWithoutResult(status -> {
            Offering offering = offering("Spring 2027");
            OfferedLab lab = part(offering, OfferedLab.class);
            lab.getRooms().clear();
        })).rootCause()
                .isInstanceOf(ConstraintViolationException.class)
                .hasMessageContaining("at least one room must be listed");
    }

    @Test
    void aDraftAndASavedVersionComeBackWithTheirSessionsAndTeachers() {
        UUID[] ids = transaction.execute(status -> {
            Offering offering = offering("Fall 2027");
            OfferedLab lab = part(offering, OfferedLab.class);
            Period monday5th = period(offering.getTerm(), DayOfWeek.MONDAY, 5, LocalTime.of(12, 15),
                    LocalTime.of(13, 5));

            TimetableDraft draft = new TimetableDraft();
            draft.setTerm(offering.getTerm());
            placeLab(draft, lab, monday5th);
            entityManager.persist(draft);

            TimetableVersion version = new TimetableVersion();
            version.setTerm(offering.getTerm());
            version.setLabel("Final");
            version.setSavedAt(Instant.parse("2026-11-19T09:00:00Z"));
            version.setHardScore(0);
            version.setSoftScore(-12);
            placeLab(version, lab, monday5th);
            entityManager.persist(version);
            return new UUID[] {draft.getId(), version.getId()};
        });

        transaction.executeWithoutResult(status -> {
            Timetable draft = entityManager.find(Timetable.class, ids[0]);
            assertThat(draft).isInstanceOf(TimetableDraft.class);
            Session session = draft.getSessions().get(0);
            assertThat(session.getLengthInPeriods()).isEqualTo(3);
            assertThat(session.getStartPeriod().getNumber()).isEqualTo(5);
            assertThat(session.getRoom().getName()).startsWith("Programming Lab");
            assertThat(session.isLocked()).isTrue();
            assertThat(draft.getInstructorAssignments().get(0).getTeacher().getName()).startsWith("Dr Rabia Khan");
            assertThat(draft.getLabEngineerAssignments().get(0).getTeacher().getName())
                    .startsWith("LE Shehla Farooq");

            TimetableVersion version = (TimetableVersion) entityManager.find(Timetable.class, ids[1]);
            assertThat(version.getLabel()).isEqualTo("Final");
            assertThat(version.getSavedAt()).isEqualTo(Instant.parse("2026-11-19T09:00:00Z"));
            assertThat(version.getSoftScore()).isEqualTo(-12);
            assertThat(version.getSessions()).hasSize(1);
        });
    }

    /**
     * AI for BESE-30-C, every part filled in as the department would, saved in a term of the given name. Each test
     * makes its own, so every name carries the term's to stay unique in the class's one database.
     */
    private Offering offering(String termName) {
        Term term = new Term();
        term.setName(termName);
        entityManager.persist(term);
        Course course = new Course();
        course.setName("Artificial Intelligence · " + termName);
        entityManager.persist(course);
        Section section = new Section();
        section.setName("BESE-30-C · " + termName);
        entityManager.persist(section);

        OfferedTheory theory = new OfferedTheory();
        theory.getSessionLengthsInPeriods().addAll(List.of(2, 1));
        theory.getInstructors().add(instructor(teacher("Lt Col Khawir", termName), 1));
        theory.getInstructors().add(instructor(teacher("Dr Abdul Razzaq", termName), 1));
        theory.getImpossibleRooms().add(room("CR-35", termName));

        OfferedLab lab = new OfferedLab();
        lab.getSessionLengthsInPeriods().add(3);
        lab.getInstructors().add(instructor(teacher("Dr Rabia Khan", termName), 1));
        lab.getLabEngineers().add(instructor(teacher("LE Shehla Farooq", termName), 1));
        RoomPriority programmingLab = new RoomPriority();
        programmingLab.setRoom(room("Programming Lab", termName));
        programmingLab.setPriority(1);
        lab.getRooms().add(programmingLab);
        lab.setOnlyListedRooms(true);

        Offering offering = new Offering();
        offering.setTerm(term);
        offering.setCourse(course);
        offering.setSection(section);
        for (OfferingPart part : List.of(theory, lab)) {
            part.setOffering(offering);
            offering.getParts().add(part);
        }
        entityManager.persist(offering);
        return offering;
    }

    /** The AI lab placed and locked from the given period, taught by its first instructor and lab engineer. */
    private void placeLab(Timetable timetable, OfferedLab lab, Period start) {
        Session session = new Session();
        session.setTimetable(timetable);
        session.setPart(lab);
        session.setLengthInPeriods(3);
        session.setStartPeriod(start);
        session.setRoom(lab.getRooms().get(0).getRoom());
        session.setLocked(true);
        timetable.getSessions().add(session);
        InstructorAssignment instructor = new InstructorAssignment();
        instructor.setTimetable(timetable);
        instructor.setPart(lab);
        instructor.setTeacher(lab.getInstructors().get(0).getTeacher());
        timetable.getInstructorAssignments().add(instructor);
        LabEngineerAssignment labEngineer = new LabEngineerAssignment();
        labEngineer.setTimetable(timetable);
        labEngineer.setLab(lab);
        labEngineer.setTeacher(lab.getLabEngineers().get(0).getTeacher());
        timetable.getLabEngineerAssignments().add(labEngineer);
    }

    private static <P extends OfferingPart> P part(Offering offering, Class<P> kind) {
        return offering.getParts().stream()
                .filter(part -> kind.isInstance(part))
                .map(part -> kind.cast(part))
                .findFirst()
                .orElseThrow();
    }

    private Teacher teacher(String name, String termName) {
        Teacher teacher = new Teacher();
        teacher.setName(name + " · " + termName);
        entityManager.persist(teacher);
        return teacher;
    }

    private Room room(String name, String termName) {
        Room room = new Room();
        room.setName(name + " · " + termName);
        entityManager.persist(room);
        return room;
    }

    private static InstructorPriority instructor(Teacher teacher, int priority) {
        InstructorPriority instructor = new InstructorPriority();
        instructor.setTeacher(teacher);
        instructor.setPriority(priority);
        return instructor;
    }

    private Period period(Term term, DayOfWeek day, int number, LocalTime startsAt, LocalTime endsAt) {
        Period period = new Period();
        period.setTerm(term);
        period.setDay(day);
        period.setNumber(number);
        period.setStartsAt(startsAt);
        period.setEndsAt(endsAt);
        entityManager.persist(period);
        return period;
    }
}
