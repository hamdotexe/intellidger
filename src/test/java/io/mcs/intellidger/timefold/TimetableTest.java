package io.mcs.intellidger.timefold;

import ai.timefold.solver.core.api.solver.SolverFactory;
import ai.timefold.solver.core.config.solver.SolverConfig;
import ai.timefold.solver.core.config.solver.termination.TerminationConfig;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.offerings.InstructorPriority;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.offerings.RoomPriority;
import io.mcs.intellidger.timefold.offerings.TheoryOffering;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.sections.RegularSection;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.terms.Term;
import io.mcs.intellidger.timefold.timetables.InstructorAssignment;
import io.mcs.intellidger.timefold.timetables.LabEngineerAssignment;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * The solver's copies of the tables: a session or an assignment offers the solver only what it may be given, and a
 * solve fills in every one of them from that and leaves locked sessions where they are. No database, no Spring.
 */
class TimetableTest {

    // Monday has four periods, Tuesday two.
    final List<Period> periods = List.of(
            period(DayOfWeek.MONDAY, 1, "08:00"), period(DayOfWeek.MONDAY, 2, "09:00"),
            period(DayOfWeek.MONDAY, 3, "10:00"), period(DayOfWeek.MONDAY, 4, "11:00"),
            period(DayOfWeek.TUESDAY, 1, "08:00"), period(DayOfWeek.TUESDAY, 2, "09:00"));

    final Room cr31 = room("CR-31");
    final Room cr36 = room("CR-36");
    final Room programmingLab = room("Programming Lab");
    final List<Room> rooms = List.of(cr31, cr36, programmingLab);

    final Teacher khawir = teacher("Lt Col Khawir");
    final Teacher rabia = teacher("Dr Rabia Khan");
    final Teacher shehla = teacher("LE Shehla Farooq");

    final RegularSection bese30c = new RegularSection(UUID.randomUUID(), "BESE-30-C",
            new Batch(UUID.randomUUID(), "BESE-30"));
    final Course ai = new Course(UUID.randomUUID(), "Artificial Intelligence");

    // The theory may use any room but the lab, preferring CR-31; Khawir first, Rabia if need be.
    final TheoryOffering aiTheory = new TheoryOffering(UUID.randomUUID(), ai, bese30c, List.of(2, 1),
            List.of(new InstructorPriority(khawir, 1), new InstructorPriority(rabia, 2)), 1,
            List.of(new RoomPriority(cr31, 1)), 1, Set.of(programmingLab), false);

    // The lab may only use the Programming Lab; Rabia teaches it with Shehla as its lab engineer.
    final LabOffering aiLab = new LabOffering(UUID.randomUUID(), ai, bese30c, List.of(3),
            List.of(new InstructorPriority(rabia, 1)), 1,
            List.of(new RoomPriority(programmingLab, 1)), 1, Set.of(), true,
            List.of(new InstructorPriority(shehla, 1)), 1);

    @Test
    void aSessionMayStartOnlyWhereItFitsInTheDay() {
        Session lab = new Session(UUID.randomUUID(), aiLab, 3, periods, rooms);
        Session single = new Session(UUID.randomUUID(), aiTheory, 1, periods, rooms);

        assertThat(lab.getPossibleStartPeriods()).extracting(start -> start.getDay() + " " + start.getNumber())
                .containsExactly("MONDAY 1", "MONDAY 2");
        assertThat(single.getPossibleStartPeriods()).hasSize(6);
    }

    @Test
    void aSessionMayUseOnlyItsPossibleRooms() {
        Session theory = new Session(UUID.randomUUID(), aiTheory, 2, periods, rooms);
        Session lab = new Session(UUID.randomUUID(), aiLab, 3, periods, rooms);

        assertThat(theory.getPossibleRooms()).containsExactly(cr31, cr36);
        assertThat(lab.getPossibleRooms()).containsExactly(programmingLab);
    }

    @Test
    void anAssignmentMayGiveOnlyListedTeachers() {
        assertThat(new InstructorAssignment(UUID.randomUUID(), aiTheory).getPossibleInstructors())
                .containsExactly(khawir, rabia);
        assertThat(new LabEngineerAssignment(UUID.randomUUID(), aiLab).getPossibleLabEngineers())
                .containsExactly(shehla);
    }

    @Test
    void aSolveFillsInEverythingFromWhatIsPossibleAndLeavesLockedSessionsAlone() {
        Session locked = new Session(UUID.randomUUID(), aiTheory, 2, periods, rooms);
        locked.setStartPeriod(periods.get(4));
        locked.setRoom(cr36);
        locked.setLocked(true);

        Timetable timetable = new Timetable();
        timetable.setTerm(new Term(false, false, true));
        timetable.setPeriods(periods);
        timetable.setRooms(rooms);
        timetable.setTeachers(List.of(khawir, rabia, shehla));
        timetable.setRegularSections(List.of(bese30c));
        timetable.setCourses(List.of(ai));
        timetable.setTheoryOfferings(List.of(aiTheory));
        timetable.setLabOfferings(List.of(aiLab));
        timetable.setSessions(List.of(locked,
                new Session(UUID.randomUUID(), aiTheory, 1, periods, rooms),
                new Session(UUID.randomUUID(), aiLab, 3, periods, rooms)));
        timetable.setInstructorAssignments(List.of(new InstructorAssignment(UUID.randomUUID(), aiTheory),
                new InstructorAssignment(UUID.randomUUID(), aiLab)));
        timetable.setLabEngineerAssignments(List.of(new LabEngineerAssignment(UUID.randomUUID(), aiLab)));

        Timetable solved = SolverFactory.<Timetable>create(new SolverConfig()
                        .withSolutionClass(Timetable.class)
                        .withEntityClasses(Session.class, InstructorAssignment.class, LabEngineerAssignment.class)
                        .withConstraintProviderClass(TimetableConstraints.class)
                        .withTerminationConfig(new TerminationConfig()
                                .withBestScoreLimit("0hard/0soft")
                                .withSpentLimit(Duration.ofSeconds(5))))
                .buildSolver()
                .solve(timetable);

        assertThat(solved.getScore().isFeasible()).isTrue();
        assertThat(solved.getSessions()).allSatisfy(session -> {
            assertThat(session.getPossibleStartPeriods()).contains(session.getStartPeriod());
            assertThat(session.getPossibleRooms()).contains(session.getRoom());
        });
        assertThat(solved.getSessions().getFirst().getStartPeriod()).isEqualTo(periods.get(4));
        assertThat(solved.getSessions().getFirst().getRoom()).isEqualTo(cr36);
        assertThat(solved.getInstructorAssignments()).allSatisfy(assignment ->
                assertThat(assignment.getPossibleInstructors()).contains(assignment.getTeacher()));
        assertThat(solved.getLabEngineerAssignments().getFirst().getTeacher()).isEqualTo(shehla);
    }

    static Period period(DayOfWeek day, int number, String startsAt) {
        LocalTime start = LocalTime.parse(startsAt);
        return new Period(UUID.randomUUID(), day, number, start, start.plusMinutes(50));
    }

    static Room room(String name) {
        return new Room(UUID.randomUUID(), name, 1);
    }

    static Teacher teacher(String name) {
        return new Teacher(UUID.randomUUID(), name);
    }
}
