package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinWeeklyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the teacher teaches at least so many periods in the week. */
@Getter
public class TeacherMinWeeklyLoad extends TeacherPreference {

    private final int minPeriodsPerWeek;

    public TeacherMinWeeklyLoad(Teacher teacher, int weight, int minPeriodsPerWeek) {
        super(teacher, weight);
        this.minPeriodsPerWeek = minPeriodsPerWeek;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMinWeeklyLoad> fetch(TeacherMinWeeklyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMinWeeklyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getMinPeriodsPerWeek()))
                .toList();
    }

    /** How many periods these sessions fall short of its weekly minimum. */
    public int missedBy(List<Session> sessions) {
        int periods = sessions.stream().mapToInt(session -> session.getLengthInPeriods()).sum();
        return Math.max(0, minPeriodsPerWeek - periods);
    }

    /** A teacher under their fewest periods in the week: soft, the weight for each period short. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.weeksTaught(factory)
                .join(TeacherMinWeeklyLoad.class,
                        Joiners.equal((teacher, sessions) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, sessions, preference) -> preference.missedBy(sessions) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, sessions, preference) -> preference.getWeight() * preference.missedBy(sessions))
                .asConstraint("Teacher under their weekly minimum");
    }
}
