package io.mcs.intellidger.timefold;

import io.mcs.intellidger.entities.batches.Batch;
import io.mcs.intellidger.entities.courses.Course;
import io.mcs.intellidger.entities.offerings.InstructorPriority;
import io.mcs.intellidger.entities.offerings.LabOffering;
import io.mcs.intellidger.entities.offerings.Offering;
import io.mcs.intellidger.entities.offerings.RoomPriority;
import io.mcs.intellidger.entities.offerings.TheoryOffering;
import io.mcs.intellidger.entities.preferences.sections.SectionDayOff;
import io.mcs.intellidger.entities.preferences.sections.SectionMinDailyLoad;
import io.mcs.intellidger.entities.preferences.teachers.TeacherMaxWeeklyLoad;
import io.mcs.intellidger.entities.preferences.teachers.TeacherOffBy;
import io.mcs.intellidger.entities.rooms.Room;
import io.mcs.intellidger.entities.rooms.RoomHardBlock;
import io.mcs.intellidger.entities.rooms.RoomSoftBlock;
import io.mcs.intellidger.entities.sections.ElectiveSection;
import io.mcs.intellidger.entities.sections.RegularSection;
import io.mcs.intellidger.entities.sections.Section;
import io.mcs.intellidger.entities.sections.SectionHardBlock;
import io.mcs.intellidger.entities.sections.SectionSoftBlock;
import io.mcs.intellidger.entities.teachers.Teacher;
import io.mcs.intellidger.entities.teachers.TeacherHardBlock;
import io.mcs.intellidger.entities.teachers.TeacherSoftBlock;
import io.mcs.intellidger.entities.teachers.TeachingLoad;
import io.mcs.intellidger.entities.terms.Term;
import io.mcs.intellidger.entities.timetables.InstructorAssignment;
import io.mcs.intellidger.entities.timetables.LabEngineerAssignment;
import io.mcs.intellidger.entities.timetables.Session;
import io.mcs.intellidger.entities.timetables.TimetableDraft;
import io.mcs.intellidger.entities.weekplan.Period;
import jakarta.persistence.EntityManager;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * A small saved term in the shape of BESE-30 C's Fall 2026, for fetches to read back: Monday's first three periods,
 * Artificial Intelligence's theory and lab for BESE-30-C, IoT-1's theory, a block of every kind, a few wishes, and a
 * draft with the lab placed and locked. Only entities here; the solver's copies of the same names stay out.
 */
final class SavedTerm {

    private SavedTerm() {
    }

    /** Saves it all, inside the caller's transaction, and returns the term's id. */
    static UUID save(EntityManager entityManager) {
        Term term = new Term();
        term.setName("Fall 2026");
        term.setInstructorsAttendLabs(false);
        term.setElectivesMutuallyExclusive(true);
        entityManager.persist(term);

        Period first = period(entityManager, term, 1, "08:00");
        Period second = period(entityManager, term, 2, "09:00");
        Period third = period(entityManager, term, 3, "10:00");

        Teacher rabia = teacher(entityManager, "Dr Rabia Khan");
        Teacher khawir = teacher(entityManager, "Lt Col Khawir");
        Teacher shehla = teacher(entityManager, "LE Shehla Farooq");
        Room cr31 = room(entityManager, "CR-31");
        Room programmingLab = room(entityManager, "Programming Lab");

        Course ai = course(entityManager, "Artificial Intelligence");
        Course iot = course(entityManager, "Internet of Things");

        Batch bese30 = new Batch();
        bese30.setName("BESE-30");
        entityManager.persist(bese30);
        RegularSection bese30c = new RegularSection();
        bese30c.setName("BESE-30-C");
        bese30c.setBatch(bese30);
        entityManager.persist(bese30c);
        ElectiveSection iot1 = new ElectiveSection();
        iot1.setName("IoT-1");
        iot1.setBatch(bese30);
        iot1.setTerm(term);
        entityManager.persist(iot1);

        TeachingLoad load = new TeachingLoad();
        load.setTerm(term);
        load.setTeacher(rabia);
        load.setMaxPeriodsPerDay(4);
        load.setMaxPeriodsPerWeek(18);
        entityManager.persist(load);

        TheoryOffering aiTheory = new TheoryOffering();
        offering(aiTheory, term, ai, bese30c, List.of(2, 1));
        aiTheory.getInstructors().addAll(List.of(instructor(khawir, 1), instructor(rabia, 2)));
        aiTheory.setInstructorPreferenceWeight(2);
        aiTheory.getRooms().add(room(cr31, 1));
        aiTheory.setRoomPreferenceWeight(1);
        aiTheory.getImpossibleRooms().add(programmingLab);
        entityManager.persist(aiTheory);

        LabOffering aiLab = new LabOffering();
        offering(aiLab, term, ai, bese30c, List.of(3));
        aiLab.getInstructors().add(instructor(rabia, 1));
        aiLab.setInstructorPreferenceWeight(0);
        aiLab.getRooms().add(room(programmingLab, 1));
        aiLab.setRoomPreferenceWeight(0);
        aiLab.setOnlyListedRooms(true);
        aiLab.getLabEngineers().add(instructor(shehla, 1));
        aiLab.setLabEngineerPreferenceWeight(1);
        entityManager.persist(aiLab);

        TheoryOffering iotTheory = new TheoryOffering();
        offering(iotTheory, term, iot, iot1, List.of(1));
        iotTheory.getInstructors().add(instructor(khawir, 1));
        iotTheory.setInstructorPreferenceWeight(0);
        iotTheory.setRoomPreferenceWeight(0);
        entityManager.persist(iotTheory);

        TeacherHardBlock rabiasFirst = new TeacherHardBlock();
        rabiasFirst.setTeacher(rabia);
        rabiasFirst.setPeriod(first);
        entityManager.persist(rabiasFirst);
        TeacherSoftBlock khawirsThird = new TeacherSoftBlock();
        khawirsThird.setTeacher(khawir);
        khawirsThird.setPeriod(third);
        khawirsThird.setWeight(2);
        entityManager.persist(khawirsThird);
        RoomHardBlock cr31sSecond = new RoomHardBlock();
        cr31sSecond.setRoom(cr31);
        cr31sSecond.setPeriod(second);
        entityManager.persist(cr31sSecond);
        RoomSoftBlock labsThird = new RoomSoftBlock();
        labsThird.setRoom(programmingLab);
        labsThird.setPeriod(third);
        labsThird.setWeight(3);
        entityManager.persist(labsThird);
        SectionHardBlock seminar = new SectionHardBlock();
        seminar.setSection(bese30c);
        seminar.setPeriod(third);
        entityManager.persist(seminar);
        SectionSoftBlock earlyStart = new SectionSoftBlock();
        earlyStart.setSection(bese30c);
        earlyStart.setPeriod(first);
        earlyStart.setWeight(1);
        entityManager.persist(earlyStart);

        SectionMinDailyLoad atLeastTwo = new SectionMinDailyLoad();
        atLeastTwo.setTerm(term);
        atLeastTwo.setSection(bese30c);
        atLeastTwo.setWeight(3);
        atLeastTwo.getMinPeriodsPerDay().put(DayOfWeek.MONDAY, 2);
        entityManager.persist(atLeastTwo);
        SectionDayOff fridayOff = new SectionDayOff();
        fridayOff.setTerm(term);
        fridayOff.setSection(bese30c);
        fridayOff.setWeight(5);
        fridayOff.setDay(DayOfWeek.FRIDAY);
        entityManager.persist(fridayOff);
        TeacherOffBy rabiaOffByOne = new TeacherOffBy();
        rabiaOffByOne.setTerm(term);
        rabiaOffByOne.setTeacher(rabia);
        rabiaOffByOne.setWeight(2);
        rabiaOffByOne.getOffByPerDay().put(DayOfWeek.MONDAY, LocalTime.of(13, 0));
        entityManager.persist(rabiaOffByOne);
        TeacherMaxWeeklyLoad khawirAtMostTwelve = new TeacherMaxWeeklyLoad();
        khawirAtMostTwelve.setTerm(term);
        khawirAtMostTwelve.setTeacher(khawir);
        khawirAtMostTwelve.setWeight(1);
        khawirAtMostTwelve.setMaxPeriodsPerWeek(12);
        entityManager.persist(khawirAtMostTwelve);

        // The lab placed and locked on Monday's first three periods, taught by Rabia with Shehla. Rabia is also down
        // for IoT, which doesn't list her, so a fetch must drop her there.
        TimetableDraft draft = new TimetableDraft();
        draft.setTerm(term);
        Session lab = new Session();
        lab.setTimetable(draft);
        lab.setOffering(aiLab);
        lab.setLengthInPeriods(3);
        lab.setStartPeriod(first);
        lab.setRoom(programmingLab);
        lab.setLocked(true);
        draft.getSessions().add(lab);
        draft.getInstructorAssignments().add(instructorAssignment(draft, aiLab, rabia));
        draft.getInstructorAssignments().add(instructorAssignment(draft, aiTheory, rabia));
        draft.getInstructorAssignments().add(instructorAssignment(draft, iotTheory, rabia));
        LabEngineerAssignment labEngineer = new LabEngineerAssignment();
        labEngineer.setTimetable(draft);
        labEngineer.setOffering(aiLab);
        labEngineer.setTeacher(shehla);
        draft.getLabEngineerAssignments().add(labEngineer);
        entityManager.persist(draft);

        return term.getId();
    }

    private static Period period(EntityManager entityManager, Term term, int number, String startsAt) {
        Period period = new Period();
        period.setTerm(term);
        period.setDay(DayOfWeek.MONDAY);
        period.setNumber(number);
        period.setStartsAt(LocalTime.parse(startsAt));
        period.setEndsAt(LocalTime.parse(startsAt).plusMinutes(50));
        entityManager.persist(period);
        return period;
    }

    private static Teacher teacher(EntityManager entityManager, String name) {
        Teacher teacher = new Teacher();
        teacher.setName(name);
        entityManager.persist(teacher);
        return teacher;
    }

    private static Room room(EntityManager entityManager, String name) {
        Room room = new Room();
        room.setName(name);
        entityManager.persist(room);
        return room;
    }

    private static Course course(EntityManager entityManager, String name) {
        Course course = new Course();
        course.setName(name);
        entityManager.persist(course);
        return course;
    }

    private static void offering(Offering offering, Term term, Course course, Section section,
            List<Integer> sessionLengthsInPeriods) {
        offering.setTerm(term);
        offering.setCourse(course);
        offering.setSection(section);
        offering.getSessionLengthsInPeriods().addAll(sessionLengthsInPeriods);
    }

    private static InstructorPriority instructor(Teacher teacher, int priority) {
        InstructorPriority instructor = new InstructorPriority();
        instructor.setTeacher(teacher);
        instructor.setPriority(priority);
        return instructor;
    }

    private static RoomPriority room(Room room, int priority) {
        RoomPriority listed = new RoomPriority();
        listed.setRoom(room);
        listed.setPriority(priority);
        return listed;
    }

    private static InstructorAssignment instructorAssignment(TimetableDraft draft, Offering offering,
            Teacher teacher) {
        InstructorAssignment assignment = new InstructorAssignment();
        assignment.setTimetable(draft);
        assignment.setOffering(offering);
        assignment.setTeacher(teacher);
        return assignment;
    }
}
