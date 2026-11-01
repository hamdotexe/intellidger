package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherEvenDaysRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the teacher's load is spread evenly over every teaching day of the week. */
public class TeacherEvenDays extends TeacherPreference {

    public TeacherEvenDays(Teacher teacher, int weight) {
        super(teacher, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherEvenDays> fetch(TeacherEvenDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherEvenDays(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight()))
                .toList();
    }

    /** How many periods these sessions are away from an even share of each teaching day. */
    public int missedBy(List<Session> sessions, Set<DayOfWeek> teachingDays) {
        return Period.awayFromEven(Session.periodsByDay(sessions), teachingDays);
    }

    /** A teacher's load spread unevenly: soft, the weight for each period a day is away from an even share. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.weeksTaught(factory)
                .join(TeacherEvenDays.class,
                        Joiners.equal((teacher, sessions) -> teacher, preference -> preference.getTeacher()))
                .join(Period.teachingDays(factory))
                .filter((teacher, sessions, preference, days) -> preference.missedBy(sessions, days) > 0)
                .penalize(HardSoftScore.ONE_SOFT, (teacher, sessions, preference, days) ->
                        preference.getWeight() * preference.missedBy(sessions, days))
                .asConstraint("Teacher's load uneven over the week");
    }
}
