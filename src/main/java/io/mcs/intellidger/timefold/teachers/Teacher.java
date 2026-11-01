package io.mcs.intellidger.timefold.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import ai.timefold.solver.core.api.score.stream.bi.BiConstraintStream;
import io.mcs.intellidger.repositories.teachers.TeacherRepository;
import io.mcs.intellidger.timefold.offerings.TheoryOffering;
import io.mcs.intellidger.timefold.terms.Term;
import io.mcs.intellidger.timefold.timetables.InstructorAssignment;
import io.mcs.intellidger.timefold.timetables.LabEngineerAssignment;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Someone who teaches, a lecturer or a lab engineer alike: what an instructor or lab engineer assignment is given. */
@Getter
@AllArgsConstructor
public class Teacher {

    private final UUID id;

    private final String name;

    /** Every teacher, as saved: they outlive terms. */
    public static List<Teacher> fetch(TeacherRepository teachers) {
        return teachers.findAll().stream()
                .map(teacher -> new Teacher(teacher.getId(), teacher.getName()))
                .toList();
    }

    /**
     * Each session a teacher must be at: every session of the offerings they teach, and of the labs they are lab
     * engineer of, but a lab's instructor only when the term says instructors attend labs.
     */
    public static BiConstraintStream<Teacher, Session> sessionsAttended(ConstraintFactory factory) {
        return factory.forEach(InstructorAssignment.class)
                .join(Session.class,
                        Joiners.equal(assignment -> assignment.getOffering(), session -> session.getOffering()))
                .ifExists(Term.class, Joiners.filtering((assignment, session, term) ->
                        session.getOffering() instanceof TheoryOffering || term.isInstructorsAttendLabs()))
                .map((assignment, session) -> assignment.getTeacher(), (assignment, session) -> session)
                .concat(labEngineered(factory));
    }

    /** Each session a teacher teaches, as their load counts it: a lab for its instructor and lab engineer alike. */
    public static BiConstraintStream<Teacher, Session> sessionsTaught(ConstraintFactory factory) {
        return factory.forEach(InstructorAssignment.class)
                .join(Session.class,
                        Joiners.equal(assignment -> assignment.getOffering(), session -> session.getOffering()))
                .map((assignment, session) -> assignment.getTeacher(), (assignment, session) -> session)
                .concat(labEngineered(factory));
    }

    /** The periods each teacher must be at a session in. */
    public static BiConstraintStream<Teacher, Set<Period>> periodsAttended(ConstraintFactory factory) {
        return sessionsAttended(factory)
                .join(Period.class,
                        Joiners.equal((teacher, session) -> session.getDay(), period -> period.getDay()),
                        Joiners.filtering((teacher, session, period) -> session.covers(period)))
                .groupBy((teacher, session, period) -> teacher,
                        ConstraintCollectors.toSet((teacher, session, period) -> period));
    }

    /** The sessions each teacher teaches, as their load counts them: none for a teacher given nothing. */
    public static BiConstraintStream<Teacher, List<Session>> weeksTaught(ConstraintFactory factory) {
        return sessionsTaught(factory)
                .groupBy((teacher, session) -> teacher, ConstraintCollectors.toList((teacher, session) -> session))
                .complement(Teacher.class, teacher -> List.of());
    }

    /** A teacher at two sessions in one period: hard, for each period two of their sessions share. */
    public static Constraint doubleBooked(ConstraintFactory factory) {
        return sessionsAttended(factory)
                .groupBy((teacher, session) -> teacher, ConstraintCollectors.toSet((teacher, session) -> session))
                .filter((teacher, sessions) -> periodsShared(sessions) > 0)
                .penalize(HardSoftScore.ONE_HARD, (teacher, sessions) -> periodsShared(sessions))
                .asConstraint("Teacher at two sessions at once");
    }

    /** Each session of a lab with its lab engineer. */
    private static BiConstraintStream<Teacher, Session> labEngineered(ConstraintFactory factory) {
        return factory.forEach(LabEngineerAssignment.class)
                .join(Session.class,
                        Joiners.equal(assignment -> assignment.getOffering(), session -> session.getOffering()))
                .map((assignment, session) -> assignment.getTeacher(), (assignment, session) -> session);
    }

    /** How many periods these sessions share, each two of them. */
    private static int periodsShared(Set<Session> sessions) {
        var listed = List.copyOf(sessions);
        int shared = 0;
        for (int first = 0; first < listed.size(); first++) {
            for (int second = first + 1; second < listed.size(); second++) {
                shared += listed.get(first).periodsSharedWith(listed.get(second));
            }
        }
        return shared;
    }
}
