package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinDailyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher teaches at least so many periods on each day listed. */
@Getter
public class TeacherMinDailyLoad extends TeacherPreference {

    private final Map<DayOfWeek, Integer> minPeriodsPerDay;

    public TeacherMinDailyLoad(Teacher teacher, int weight, Map<DayOfWeek, Integer> minPeriodsPerDay) {
        super(teacher, weight);
        this.minPeriodsPerDay = minPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMinDailyLoad> fetch(TeacherMinDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMinDailyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMinPeriodsPerDay())))
                .toList();
    }

    /** How many periods these sessions fall short of its minimums, on each day it lists. */
    public int missedBy(List<Session> sessions) {
        var loads = Session.periodsByDay(sessions);
        return minPeriodsPerDay.entrySet().stream()
                .mapToInt(entry -> Math.max(0, entry.getValue() - loads.getOrDefault(entry.getKey(), 0)))
                .sum();
    }

    /** A teacher under their fewest periods on a day: soft, the weight for each period short. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.weeksTaught(factory)
                .join(TeacherMinDailyLoad.class,
                        Joiners.equal((teacher, sessions) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, sessions, preference) -> preference.missedBy(sessions) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, sessions, preference) -> preference.getWeight() * preference.missedBy(sessions))
                .asConstraint("Teacher under their daily minimum");
    }
}
