package io.mcs.intellidger.timefold;

import io.mcs.intellidger.repositories.batches.BatchRepository;
import io.mcs.intellidger.repositories.courses.CourseRepository;
import io.mcs.intellidger.repositories.offerings.LabOfferingRepository;
import io.mcs.intellidger.repositories.offerings.TheoryOfferingRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionCourseSpreadRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionDayOffRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionEvenDaysRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionFreeDaysRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionMaxDailyLoadRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionMinDailyLoadRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionNoIdlePeriodsRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionNotBeforeRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionOffByRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionPeriodsInARowRepository;
import io.mcs.intellidger.repositories.preferences.sections.SectionSameRoomRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherDayOffRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherEvenDaysRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherFreeDaysRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxDailyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxWeeklyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinDailyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinWeeklyLoadRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherNoIdlePeriodsRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherNotBeforeRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherOffByRepository;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherPeriodsInARowRepository;
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
import io.mcs.intellidger.repositories.timetables.TimetableVersionRepository;
import io.mcs.intellidger.repositories.weekplan.PeriodRepository;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.offerings.TheoryOffering;
import io.mcs.intellidger.timefold.preferences.sections.SectionCourseSpread;
import io.mcs.intellidger.timefold.preferences.sections.SectionDayOff;
import io.mcs.intellidger.timefold.preferences.sections.SectionEvenDays;
import io.mcs.intellidger.timefold.preferences.sections.SectionFreeDays;
import io.mcs.intellidger.timefold.preferences.sections.SectionMaxDailyLoad;
import io.mcs.intellidger.timefold.preferences.sections.SectionMinDailyLoad;
import io.mcs.intellidger.timefold.preferences.sections.SectionNoIdlePeriods;
import io.mcs.intellidger.timefold.preferences.sections.SectionNotBefore;
import io.mcs.intellidger.timefold.preferences.sections.SectionOffBy;
import io.mcs.intellidger.timefold.preferences.sections.SectionPeriodsInARow;
import io.mcs.intellidger.timefold.preferences.sections.SectionSameRoom;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherDayOff;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherEvenDays;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherFreeDays;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherMaxDailyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherMaxWeeklyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherMinDailyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherMinWeeklyLoad;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherNoIdlePeriods;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherNotBefore;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherOffBy;
import io.mcs.intellidger.timefold.preferences.teachers.TeacherPeriodsInARow;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Fills a {@link Timetable} with a term's whole problem: everything it is solved against, and its draft or one of its
 * saved versions. Each table comes through its own {@code fetch}, in the order they refer to each other, since a
 * fetch finds the rows its rows point to among those already in.
 */
@Component
@RequiredArgsConstructor
public class LoadProblem {

    private final TermRepository terms;
    private final PeriodRepository periods;
    private final TeacherRepository teachers;
    private final RoomRepository rooms;
    private final CourseRepository courses;
    private final BatchRepository batches;
    private final RegularSectionRepository regularSections;
    private final ElectiveSectionRepository electiveSections;
    private final TeachingLoadRepository teachingLoads;
    private final TeacherHardBlockRepository teacherHardBlocks;
    private final TeacherSoftBlockRepository teacherSoftBlocks;
    private final RoomHardBlockRepository roomHardBlocks;
    private final RoomSoftBlockRepository roomSoftBlocks;
    private final SectionHardBlockRepository sectionHardBlocks;
    private final SectionSoftBlockRepository sectionSoftBlocks;
    private final TheoryOfferingRepository theoryOfferings;
    private final LabOfferingRepository labOfferings;
    private final SectionCourseSpreadRepository sectionCourseSpreads;
    private final SectionDayOffRepository sectionDaysOff;
    private final SectionEvenDaysRepository sectionEvenDays;
    private final SectionFreeDaysRepository sectionFreeDays;
    private final SectionMinDailyLoadRepository sectionMinDailyLoads;
    private final SectionMaxDailyLoadRepository sectionMaxDailyLoads;
    private final SectionNoIdlePeriodsRepository sectionNoIdlePeriods;
    private final SectionNotBeforeRepository sectionNotBefore;
    private final SectionOffByRepository sectionOffBy;
    private final SectionPeriodsInARowRepository sectionPeriodsInARow;
    private final SectionSameRoomRepository sectionSameRooms;
    private final TeacherDayOffRepository teacherDaysOff;
    private final TeacherEvenDaysRepository teacherEvenDays;
    private final TeacherFreeDaysRepository teacherFreeDays;
    private final TeacherMinDailyLoadRepository teacherMinDailyLoads;
    private final TeacherMaxDailyLoadRepository teacherMaxDailyLoads;
    private final TeacherMinWeeklyLoadRepository teacherMinWeeklyLoads;
    private final TeacherMaxWeeklyLoadRepository teacherMaxWeeklyLoads;
    private final TeacherNoIdlePeriodsRepository teacherNoIdlePeriods;
    private final TeacherNotBeforeRepository teacherNotBefore;
    private final TeacherOffByRepository teacherOffBy;
    private final TeacherPeriodsInARowRepository teacherPeriodsInARow;
    private final TimetableDraftRepository drafts;
    private final TimetableVersionRepository versions;

    /**
     * The term's problem with its draft, ready to solve and needing the database no more. Called from a service,
     * inside its transaction, which the fetches need to read their rows' collections.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public Timetable load(UUID termId) {
        Timetable timetable = inputs(termId);
        // Last, what the solver decides: one session per session length of each offering, one teacher per role, the
        // draft's where it has them.
        timetable.setSessions(Session.fetch(drafts, termId, timetable));
        timetable.setInstructorAssignments(InstructorAssignment.fetch(drafts, termId, timetable));
        timetable.setLabEngineerAssignments(LabEngineerAssignment.fetch(drafts, termId, timetable));
        return timetable;
    }

    /**
     * A saved version against its term as the term stands now, to be checked again: placed as the version has it,
     * anything added to the term since not placed at all. Called from a service, inside its transaction.
     */
    @Transactional(propagation = Propagation.MANDATORY)
    public Timetable loadVersion(UUID versionId) {
        Timetable timetable = inputs(versions.findById(versionId).orElseThrow().getTerm().getId());
        timetable.setSessions(Session.fetch(versions, versionId, timetable));
        timetable.setInstructorAssignments(InstructorAssignment.fetch(versions, versionId, timetable));
        timetable.setLabEngineerAssignments(LabEngineerAssignment.fetch(versions, versionId, timetable));
        return timetable;
    }

    /** Everything the term is solved against: every list but the sessions and assignments the solver decides. */
    private Timetable inputs(UUID termId) {
        Timetable timetable = new Timetable();

        // What every other row points to.
        timetable.setTerm(Term.fetch(terms, termId));
        timetable.setPeriods(Period.fetch(periods, termId));
        timetable.setTeachers(Teacher.fetch(teachers));
        timetable.setRooms(Room.fetch(rooms));
        timetable.setCourses(Course.fetch(courses));
        timetable.setBatches(Batch.fetch(batches));

        // Sections, each in its batch.
        timetable.setRegularSections(RegularSection.fetch(regularSections, timetable));
        timetable.setElectiveSections(ElectiveSection.fetch(electiveSections, termId, timetable));

        // Loads and blocks, on periods, teachers, rooms and sections.
        timetable.setTeachingLoads(TeachingLoad.fetch(teachingLoads, termId, timetable));
        timetable.setTeacherHardBlocks(TeacherHardBlock.fetch(teacherHardBlocks, termId, timetable));
        timetable.setTeacherSoftBlocks(TeacherSoftBlock.fetch(teacherSoftBlocks, termId, timetable));
        timetable.setRoomHardBlocks(RoomHardBlock.fetch(roomHardBlocks, termId, timetable));
        timetable.setRoomSoftBlocks(RoomSoftBlock.fetch(roomSoftBlocks, termId, timetable));
        timetable.setSectionHardBlocks(SectionHardBlock.fetch(sectionHardBlocks, termId, timetable));
        timetable.setSectionSoftBlocks(SectionSoftBlock.fetch(sectionSoftBlocks, termId, timetable));

        // Offerings: a course for a section, its teachers and rooms by priority.
        timetable.setTheoryOfferings(TheoryOffering.fetch(theoryOfferings, termId, timetable));
        timetable.setLabOfferings(LabOffering.fetch(labOfferings, termId, timetable));

        // Preferences, each on its section or teacher.
        timetable.setSectionCourseSpreads(SectionCourseSpread.fetch(sectionCourseSpreads, termId, timetable));
        timetable.setSectionDaysOff(SectionDayOff.fetch(sectionDaysOff, termId, timetable));
        timetable.setSectionEvenDays(SectionEvenDays.fetch(sectionEvenDays, termId, timetable));
        timetable.setSectionFreeDays(SectionFreeDays.fetch(sectionFreeDays, termId, timetable));
        timetable.setSectionMinDailyLoads(SectionMinDailyLoad.fetch(sectionMinDailyLoads, termId, timetable));
        timetable.setSectionMaxDailyLoads(SectionMaxDailyLoad.fetch(sectionMaxDailyLoads, termId, timetable));
        timetable.setSectionNoIdlePeriods(SectionNoIdlePeriods.fetch(sectionNoIdlePeriods, termId, timetable));
        timetable.setSectionNotBefore(SectionNotBefore.fetch(sectionNotBefore, termId, timetable));
        timetable.setSectionOffBy(SectionOffBy.fetch(sectionOffBy, termId, timetable));
        timetable.setSectionPeriodsInARow(SectionPeriodsInARow.fetch(sectionPeriodsInARow, termId, timetable));
        timetable.setSectionSameRooms(SectionSameRoom.fetch(sectionSameRooms, termId, timetable));
        timetable.setTeacherDaysOff(TeacherDayOff.fetch(teacherDaysOff, termId, timetable));
        timetable.setTeacherEvenDays(TeacherEvenDays.fetch(teacherEvenDays, termId, timetable));
        timetable.setTeacherFreeDays(TeacherFreeDays.fetch(teacherFreeDays, termId, timetable));
        timetable.setTeacherMinDailyLoads(TeacherMinDailyLoad.fetch(teacherMinDailyLoads, termId, timetable));
        timetable.setTeacherMaxDailyLoads(TeacherMaxDailyLoad.fetch(teacherMaxDailyLoads, termId, timetable));
        timetable.setTeacherMinWeeklyLoads(TeacherMinWeeklyLoad.fetch(teacherMinWeeklyLoads, termId, timetable));
        timetable.setTeacherMaxWeeklyLoads(TeacherMaxWeeklyLoad.fetch(teacherMaxWeeklyLoads, termId, timetable));
        timetable.setTeacherNoIdlePeriods(TeacherNoIdlePeriods.fetch(teacherNoIdlePeriods, termId, timetable));
        timetable.setTeacherNotBefore(TeacherNotBefore.fetch(teacherNotBefore, termId, timetable));
        timetable.setTeacherOffBy(TeacherOffBy.fetch(teacherOffBy, termId, timetable));
        timetable.setTeacherPeriodsInARow(TeacherPeriodsInARow.fetch(teacherPeriodsInARow, termId, timetable));

        return timetable;
    }
}
