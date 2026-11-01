package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxWeeklyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the teacher teaches at most so many periods in the week. */
@Getter
public class TeacherMaxWeeklyLoad extends TeacherPreference {

    private final int maxPeriodsPerWeek;

    public TeacherMaxWeeklyLoad(Teacher teacher, int weight, int maxPeriodsPerWeek) {
        super(teacher, weight);
        this.maxPeriodsPerWeek = maxPeriodsPerWeek;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMaxWeeklyLoad> fetch(TeacherMaxWeeklyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMaxWeeklyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getMaxPeriodsPerWeek()))
                .toList();
    }

    /** How many periods these sessions go over its weekly maximum. */
    public int missedBy(List<Session> sessions) {
        int periods = sessions.stream().mapToInt(session -> session.getLengthInPeriods()).sum();
        return Math.max(0, periods - maxPeriodsPerWeek);
    }

    /** A teacher over their most periods in the week: soft, the weight for each period over. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.weeksTaught(factory)
                .join(TeacherMaxWeeklyLoad.class,
                        Joiners.equal((teacher, sessions) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, sessions, preference) -> preference.missedBy(sessions) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, sessions, preference) -> preference.getWeight() * preference.missedBy(sessions))
                .asConstraint("Teacher over their weekly maximum");
    }
}
