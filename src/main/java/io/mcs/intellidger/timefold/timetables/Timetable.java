package io.mcs.intellidger.timefold.timetables;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.ProblemFactProperty;
import ai.timefold.solver.core.api.score.HardSoftScore;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.offerings.Offering;
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
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.sections.SectionHardBlock;
import io.mcs.intellidger.timefold.sections.SectionSoftBlock;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.teachers.TeacherHardBlock;
import io.mcs.intellidger.timefold.teachers.TeacherSoftBlock;
import io.mcs.intellidger.timefold.teachers.TeachingLoad;
import io.mcs.intellidger.timefold.terms.Term;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * A term's draft as the solver sees it, one list per table: everything it is solved against, the sessions and
 * assignments it decides, and the score it gives them. Built for a run and kept in memory only while it lasts.
 */
@PlanningSolution
@Getter
@Setter
public class Timetable {

    @ProblemFactProperty
    private Term term;

    @ProblemFactCollectionProperty
    private List<Period> periods = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Teacher> teachers = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeachingLoad> teachingLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherHardBlock> teacherHardBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherSoftBlock> teacherSoftBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Room> rooms = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<RoomHardBlock> roomHardBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<RoomSoftBlock> roomSoftBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Course> courses = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<Batch> batches = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<RegularSection> regularSections = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<ElectiveSection> electiveSections = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionHardBlock> sectionHardBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionSoftBlock> sectionSoftBlocks = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TheoryOffering> theoryOfferings = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<LabOffering> labOfferings = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionCourseSpread> sectionCourseSpreads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionDayOff> sectionDaysOff = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionEvenDays> sectionEvenDays = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionFreeDays> sectionFreeDays = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionMinDailyLoad> sectionMinDailyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionMaxDailyLoad> sectionMaxDailyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionNoIdlePeriods> sectionNoIdlePeriods = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionNotBefore> sectionNotBefore = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionOffBy> sectionOffBy = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionPeriodsInARow> sectionPeriodsInARow = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<SectionSameRoom> sectionSameRooms = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherDayOff> teacherDaysOff = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherEvenDays> teacherEvenDays = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherFreeDays> teacherFreeDays = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherMinDailyLoad> teacherMinDailyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherMaxDailyLoad> teacherMaxDailyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherMinWeeklyLoad> teacherMinWeeklyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherMaxWeeklyLoad> teacherMaxWeeklyLoads = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherNoIdlePeriods> teacherNoIdlePeriods = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherNotBefore> teacherNotBefore = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherOffBy> teacherOffBy = new ArrayList<>();

    @ProblemFactCollectionProperty
    private List<TeacherPeriodsInARow> teacherPeriodsInARow = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<Session> sessions = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<InstructorAssignment> instructorAssignments = new ArrayList<>();

    @PlanningEntityCollectionProperty
    private List<LabEngineerAssignment> labEngineerAssignments = new ArrayList<>();

    /** Hard below 0 means a hard rule is broken: the run has failed, and nothing of it is kept. */
    @PlanningScore
    private HardSoftScore score;

    /**
     * Whether every session is placed and every offering has its teachers. One that isn't is never kept, whatever its
     * score, since a session not placed breaks no rule and costs nothing.
     */
    public boolean isComplete() {
        return sessions.stream().allMatch(session -> session.getStartPeriod() != null && session.getRoom() != null)
                && instructorAssignments.stream().allMatch(assignment -> assignment.getTeacher() != null)
                && labEngineerAssignments.stream().allMatch(assignment -> assignment.getTeacher() != null);
    }

    /** Every offering, theory and lab. */
    public List<Offering> getOfferings() {
        return Stream.<Offering>concat(theoryOfferings.stream(), labOfferings.stream()).toList();
    }

    /** The copy of the period with this id: what a later fetch refers to, so every row shares one copy. */
    public Period period(UUID id) {
        return periods.stream().filter(period -> period.getId().equals(id)).findFirst().orElseThrow();
    }

    /** The copy of the teacher with this id. */
    public Teacher teacher(UUID id) {
        return teachers.stream().filter(teacher -> teacher.getId().equals(id)).findFirst().orElseThrow();
    }

    /** The copy of the room with this id. */
    public Room room(UUID id) {
        return rooms.stream().filter(room -> room.getId().equals(id)).findFirst().orElseThrow();
    }

    /** The copy of the course with this id. */
    public Course course(UUID id) {
        return courses.stream().filter(course -> course.getId().equals(id)).findFirst().orElseThrow();
    }

    /** The copy of the batch with this id. */
    public Batch batch(UUID id) {
        return batches.stream().filter(batch -> batch.getId().equals(id)).findFirst().orElseThrow();
    }

    /** The copy of the section with this id, regular or elective. */
    public Section section(UUID id) {
        return Stream.<Section>concat(regularSections.stream(), electiveSections.stream())
                .filter(section -> section.getId().equals(id))
                .findFirst()
                .orElseThrow();
    }
}
