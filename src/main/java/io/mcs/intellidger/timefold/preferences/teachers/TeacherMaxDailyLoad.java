package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxDailyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher teaches at most so many periods on each day listed. */
@Getter
public class TeacherMaxDailyLoad extends TeacherPreference {

    private final Map<DayOfWeek, Integer> maxPeriodsPerDay;

    public TeacherMaxDailyLoad(Teacher teacher, int weight, Map<DayOfWeek, Integer> maxPeriodsPerDay) {
        super(teacher, weight);
        this.maxPeriodsPerDay = maxPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMaxDailyLoad> fetch(TeacherMaxDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMaxDailyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMaxPeriodsPerDay())))
                .toList();
    }

    /** How many periods these sessions go over its maximums, on each day it lists. */
    public int missedBy(List<Session> sessions) {
        var loads = Session.periodsByDay(sessions);
        return maxPeriodsPerDay.entrySet().stream()
                .mapToInt(entry -> Math.max(0, loads.getOrDefault(entry.getKey(), 0) - entry.getValue()))
                .sum();
    }

    /** A teacher over their most periods on a day: soft, the weight for each period over. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.weeksTaught(factory)
                .join(TeacherMaxDailyLoad.class,
                        Joiners.equal((teacher, sessions) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, sessions, preference) -> preference.missedBy(sessions) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, sessions, preference) -> preference.getWeight() * preference.missedBy(sessions))
                .asConstraint("Teacher over their daily maximum");
    }
}
