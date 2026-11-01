package io.mcs.intellidger.timefold;

import io.mcs.intellidger.DatabaseTestBase;
import io.mcs.intellidger.repositories.batches.BatchRepository;
import io.mcs.intellidger.repositories.courses.CourseRepository;
import io.mcs.intellidger.repositories.offerings.LabOfferingRepository;
import io.mcs.intellidger.repositories.offerings.TheoryOfferingRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionDayOffRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionMinDailyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxWeeklyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherOffByRepository;
import io.mcs.intellidger.repositories.rooms.RoomHardBlockRepository;
import io.mcs.intellidger.repositories.rooms.RoomRepository;
import io.mcs.intellidger.repositories.rooms.RoomSoftBlockRepository;
import io.mcs.intellidger.repositories.sections.ElectiveSectionRepository;
import io.mcs.intellidger.repositories.sections.RegularSectionRepository;
import io.mcs.intellidger.repositories.sections.SectionHardBlockRepository;
import io.mcs.intellidger.repositories.sections.SectionSoftBlockRepository;
import io.mcs.intellidger.repositories.teachers.TeacherHardBlockRepository;
import io.mcs.intellidger.repositories.teachers.TeacherRepository;
import io.mcs.intellidger.repositories.teachers.TeacherSoftBlockRepository;
import io.mcs.intellidger.repositories.teachers.TeachingLoadRepository;
import io.mcs.intellidger.repositories.terms.TermRepository;
import io.mcs.intellidger.repositories.timetables.TimetableDraftRepository;
import io.mcs.intellidger.repositories.weekplan.PeriodRepository;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.offerings.Offering;
import io.mcs.intellidger.timefold.offerings.TheoryOffering;
import io.mcs.intellidger.timefold.preferences.sections.SectionDayOff;
import io.mcs.intellidger.timefold.preferences.sections.SectionMinDailyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherMaxWeeklyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherOffBy;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.rooms.RoomHardBlock;
import io.mcs.intellidger.timefold.rooms.RoomSoftBlock;
import io.mcs.intellidger.timefold.sections.ElectiveSection;
import io.mcs.intellidger.timefold.sections.RegularSection;
import io.mcs.intellidger.timefold.sections.SectionHardBlock;
import io.mcs.intellidger.timefold.sections.SectionSoftBlock;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.teachers.TeacherHardBlock;
import io.mcs.intellidger.timefold.teachers.TeacherSoftBlock;
import io.mcs.intellidger.timefold.teachers.TeachingLoad;
import io.mcs.intellidger.timefold.terms.Term;
import io.mcs.intellidger.timefold.timetables.InstructorAssignment;
import io.mcs.intellidger.timefold.timetables.LabEngineerAssignment;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

/**
 * Every copy fetches its table back as saved, in the order a run reads them, and rows that point at the same thing
 * share one copy of it. The draft's sessions and assignments come back where it has them, and new where it doesn't.
 */
class TimetableFetchTest extends DatabaseTestBase {

    @Autowired
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transaction;

    @Autowired
    TermRepository terms;

    @Autowired
    PeriodRepository periods;

    @Autowired
    TeacherRepository teachers;

    @Autowired
    TeachingLoadRepository teachingLoads;

    @Autowired
    TeacherHardBlockRepository teacherHardBlocks;

    @Autowired
    TeacherSoftBlockRepository teacherSoftBlocks;

    @Autowired
    RoomRepository rooms;

    @Autowired
    RoomHardBlockRepository roomHardBlocks;

    @Autowired
    RoomSoftBlockRepository roomSoftBlocks;

    @Autowired
    CourseRepository courses;

    @Autowired
    BatchRepository batches;

    @Autowired
    RegularSectionRepository regularSections;

    @Autowired
    ElectiveSectionRepository electiveSections;

    @Autowired
    SectionHardBlockRepository sectionHardBlocks;

    @Autowired
    SectionSoftBlockRepository sectionSoftBlocks;

    @Autowired
    TheoryOfferingRepository theoryOfferings;

    @Autowired
    LabOfferingRepository labOfferings;

    @Autowired
    SectionMinDailyLoadRepository sectionMinDailyLoads;

    @Autowired
    SectionDayOffRepository sectionDaysOff;

    @Autowired
    TeacherOffByRepository teacherOffBy;

    @Autowired
    TeacherMaxWeeklyLoadRepository teacherMaxWeeklyLoads;

    @Autowired
    TimetableDraftRepository drafts;

    @Test
    void aSavedTermIsFetchedWholeWithOneCopyOfEachRow() {
        UUID termId = transaction.execute(status -> SavedTerm.save(entityManager));
        Timetable timetable = transaction.execute(status -> fetch(termId));

        assertThat(timetable.getTerm().isInstructorsAttendLabs()).isFalse();
        assertThat(timetable.getTerm().isElectivesMutuallyExclusive()).isTrue();
        assertThat(timetable.getPeriods()).extracting(period -> period.getNumber()).containsExactlyInAnyOrder(1, 2, 3);
        Period first = periodNumbered(timetable, 1);
        Period second = periodNumbered(timetable, 2);
        Period third = periodNumbered(timetable, 3);
        assertThat(first.getDay()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(first.getStartsAt()).isEqualTo(LocalTime.of(8, 0));

        Teacher rabia = teacherNamed(timetable.getTeachers(), "Dr Rabia Khan");
        Teacher khawir = teacherNamed(timetable.getTeachers(), "Lt Col Khawir");
        Teacher shehla = teacherNamed(timetable.getTeachers(), "LE Shehla Farooq");
        Room cr31 = roomNamed(timetable.getRooms(), "CR-31");
        Room programmingLab = roomNamed(timetable.getRooms(), "Programming Lab");
        Batch bese30 = timetable.getBatches().getFirst();
        RegularSection bese30c = timetable.getRegularSections().getFirst();
        ElectiveSection iot1 = timetable.getElectiveSections().getFirst();
        assertThat(cr31.getMaxSectionsAtOnce()).isEqualTo(1);
        assertThat(bese30c.getBatch()).isSameAs(bese30);
        assertThat(iot1.getBatch()).isSameAs(bese30);

        TeachingLoad load = timetable.getTeachingLoads().getFirst();
        assertThat(load.getTeacher()).isSameAs(rabia);
        assertThat(load.getMaxPeriodsPerDay()).isEqualTo(4);
        assertThat(load.getMaxPeriodsPerWeek()).isEqualTo(18);

        TheoryOffering aiTheory = timetable.getTheoryOfferings().stream()
                .filter(offering -> offering.getSection() == bese30c)
                .findFirst()
                .orElseThrow();
        assertThat(aiTheory.getCourse().getName()).isEqualTo("Artificial Intelligence");
        assertThat(aiTheory.getSessionLengthsInPeriods()).containsExactly(2, 1);
        assertThat(aiTheory.getInstructors()).extracting(listed -> listed.getTeacher(), listed -> listed.getPriority())
                .containsExactly(tuple(khawir, 1), tuple(rabia, 2));
        assertThat(aiTheory.getInstructorPreferenceWeight()).isEqualTo(2);
        assertThat(aiTheory.getRooms()).extracting(listed -> listed.getRoom()).containsExactly(cr31);
        assertThat(aiTheory.getRoomPreferenceWeight()).isEqualTo(1);
        assertThat(aiTheory.getImpossibleRooms()).containsExactly(programmingLab);
        assertThat(aiTheory.isOnlyListedRooms()).isFalse();

        LabOffering aiLab = timetable.getLabOfferings().getFirst();
        assertThat(aiLab.getCourse()).isSameAs(aiTheory.getCourse());
        assertThat(aiLab.isOnlyListedRooms()).isTrue();
        assertThat(aiLab.getLabEngineers()).extracting(listed -> listed.getTeacher()).containsExactly(shehla);
        assertThat(aiLab.getLabEngineerPreferenceWeight()).isEqualTo(1);

        TeacherHardBlock teacherHardBlock = timetable.getTeacherHardBlocks().getFirst();
        assertThat(teacherHardBlock.getTeacher()).isSameAs(rabia);
        assertThat(teacherHardBlock.getPeriod()).isSameAs(first);
        TeacherSoftBlock teacherSoftBlock = timetable.getTeacherSoftBlocks().getFirst();
        assertThat(teacherSoftBlock.getTeacher()).isSameAs(khawir);
        assertThat(teacherSoftBlock.getPeriod()).isSameAs(third);
        assertThat(teacherSoftBlock.getWeight()).isEqualTo(2);
        RoomHardBlock roomHardBlock = timetable.getRoomHardBlocks().getFirst();
        assertThat(roomHardBlock.getRoom()).isSameAs(cr31);
        assertThat(roomHardBlock.getPeriod()).isSameAs(second);
        RoomSoftBlock roomSoftBlock = timetable.getRoomSoftBlocks().getFirst();
        assertThat(roomSoftBlock.getRoom()).isSameAs(programmingLab);
        assertThat(roomSoftBlock.getWeight()).isEqualTo(3);
        SectionHardBlock sectionHardBlock = timetable.getSectionHardBlocks().getFirst();
        assertThat(sectionHardBlock.getSection()).isSameAs(bese30c);
        assertThat(sectionHardBlock.getPeriod()).isSameAs(third);
        SectionSoftBlock sectionSoftBlock = timetable.getSectionSoftBlocks().getFirst();
        assertThat(sectionSoftBlock.getSection()).isSameAs(bese30c);
        assertThat(sectionSoftBlock.getWeight()).isEqualTo(1);

        SectionMinDailyLoad atLeastTwo = timetable.getSectionMinDailyLoads().getFirst();
        assertThat(atLeastTwo.getSection()).isSameAs(bese30c);
        assertThat(atLeastTwo.getWeight()).isEqualTo(3);
        assertThat(atLeastTwo.getMinPeriodsPerDay()).isEqualTo(Map.of(DayOfWeek.MONDAY, 2));
        SectionDayOff fridayOff = timetable.getSectionDaysOff().getFirst();
        assertThat(fridayOff.getDay()).isEqualTo(DayOfWeek.FRIDAY);
        assertThat(fridayOff.getWeight()).isEqualTo(5);
        TeacherOffBy rabiaOffByOne = timetable.getTeacherOffBy().getFirst();
        assertThat(rabiaOffByOne.getTeacher()).isSameAs(rabia);
        assertThat(rabiaOffByOne.getOffByPerDay()).isEqualTo(Map.of(DayOfWeek.MONDAY, LocalTime.of(13, 0)));
        TeacherMaxWeeklyLoad khawirAtMostTwelve = timetable.getTeacherMaxWeeklyLoads().getFirst();
        assertThat(khawirAtMostTwelve.getTeacher()).isSameAs(khawir);
        assertThat(khawirAtMostTwelve.getMaxPeriodsPerWeek()).isEqualTo(12);

        // One session per session length: the lab as the draft placed and locked it, the rest not placed yet.
        assertThat(timetable.getSessions()).hasSize(4);
        Session lab = sessionsOf(timetable, aiLab).getFirst();
        assertThat(lab.getStartPeriod()).isSameAs(first);
        assertThat(lab.getRoom()).isSameAs(programmingLab);
        assertThat(lab.isLocked()).isTrue();
        assertThat(lab.getPossibleRooms()).containsExactly(programmingLab);
        assertThat(sessionsOf(timetable, aiTheory)).extracting(session -> session.getLengthInPeriods())
                .containsExactly(2, 1);
        assertThat(sessionsOf(timetable, aiTheory)).allSatisfy(session -> {
            assertThat(session.getStartPeriod()).isNull();
            assertThat(session.isLocked()).isFalse();
            assertThat(session.getPossibleRooms()).containsExactly(cr31);
        });

        // Rabia stays on what lists her; on IoT, which doesn't, she is dropped.
        assertThat(assignmentOf(timetable, aiLab).getTeacher()).isSameAs(rabia);
        assertThat(assignmentOf(timetable, aiTheory).getTeacher()).isSameAs(rabia);
        TheoryOffering iotTheory = timetable.getTheoryOfferings().stream()
                .filter(offering -> offering.getSection() == iot1)
                .findFirst()
                .orElseThrow();
        assertThat(assignmentOf(timetable, iotTheory).getTeacher()).isNull();
        LabEngineerAssignment labEngineer = timetable.getLabEngineerAssignments().getFirst();
        assertThat(labEngineer.getOffering()).isSameAs(aiLab);
        assertThat(labEngineer.getTeacher()).isSameAs(shehla);
    }

    /** What a run reads, in the order it reads it: each fetch finds what it points at among the ones before. */
    Timetable fetch(UUID termId) {
        Timetable timetable = new Timetable();
        timetable.setTerm(Term.fetch(terms, termId));
        timetable.setPeriods(Period.fetch(periods, termId));
        timetable.setTeachers(Teacher.fetch(teachers));
        timetable.setRooms(Room.fetch(rooms));
        timetable.setCourses(Course.fetch(courses));
        timetable.setBatches(Batch.fetch(batches));
        timetable.setRegularSections(RegularSection.fetch(regularSections, timetable));
        timetable.setElectiveSections(ElectiveSection.fetch(electiveSections, termId, timetable));
        timetable.setTeachingLoads(TeachingLoad.fetch(teachingLoads, termId, timetable));
        timetable.setTeacherHardBlocks(TeacherHardBlock.fetch(teacherHardBlocks, termId, timetable));
        timetable.setTeacherSoftBlocks(TeacherSoftBlock.fetch(teacherSoftBlocks, termId, timetable));
        timetable.setRoomHardBlocks(RoomHardBlock.fetch(roomHardBlocks, termId, timetable));
        timetable.setRoomSoftBlocks(RoomSoftBlock.fetch(roomSoftBlocks, termId, timetable));
        timetable.setSectionHardBlocks(SectionHardBlock.fetch(sectionHardBlocks, termId, timetable));
        timetable.setSectionSoftBlocks(SectionSoftBlock.fetch(sectionSoftBlocks, termId, timetable));
        timetable.setTheoryOfferings(TheoryOffering.fetch(theoryOfferings, termId, timetable));
        timetable.setLabOfferings(LabOffering.fetch(labOfferings, termId, timetable));
        timetable.setSectionMinDailyLoads(SectionMinDailyLoad.fetch(sectionMinDailyLoads, termId, timetable));
        timetable.setSectionDaysOff(SectionDayOff.fetch(sectionDaysOff, termId, timetable));
        timetable.setTeacherOffBy(TeacherOffBy.fetch(teacherOffBy, termId, timetable));
        timetable.setTeacherMaxWeeklyLoads(TeacherMaxWeeklyLoad.fetch(teacherMaxWeeklyLoads, termId, timetable));
        timetable.setSessions(Session.fetch(drafts, termId, timetable));
        timetable.setInstructorAssignments(InstructorAssignment.fetch(drafts, termId, timetable));
        timetable.setLabEngineerAssignments(LabEngineerAssignment.fetch(drafts, termId, timetable));
        return timetable;
    }

    static Period periodNumbered(Timetable timetable, int number) {
        return timetable.getPeriods().stream().filter(period -> period.getNumber() == number).findFirst().orElseThrow();
    }

    static Teacher teacherNamed(List<Teacher> teachers, String name) {
        return teachers.stream().filter(teacher -> teacher.getName().equals(name)).findFirst().orElseThrow();
    }

    static Room roomNamed(List<Room> rooms, String name) {
        return rooms.stream().filter(room -> room.getName().equals(name)).findFirst().orElseThrow();
    }

    static List<Session> sessionsOf(Timetable timetable, Offering offering) {
        return timetable.getSessions().stream().filter(session -> session.getOffering() == offering).toList();
    }

    static InstructorAssignment assignmentOf(Timetable timetable, Offering offering) {
        return timetable.getInstructorAssignments().stream()
                .filter(assignment -> assignment.getOffering() == offering)
                .findFirst()
                .orElseThrow();
    }
}
