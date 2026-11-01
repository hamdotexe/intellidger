package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherNotBeforeRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** That the teacher starts no earlier than a time on each day listed. */
@Getter
public class TeacherNotBefore extends TeacherPreference {

    private final Map<DayOfWeek, LocalTime> notBeforePerDay;

    public TeacherNotBefore(Teacher teacher, int weight, Map<DayOfWeek, LocalTime> notBeforePerDay) {
        super(teacher, weight);
        this.notBeforePerDay = notBeforePerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherNotBefore> fetch(TeacherNotBeforeRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherNotBefore(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getNotBeforePerDay())))
                .toList();
    }

    /** How many of these periods start before its time on their day. */
    public int missedBy(Set<Period> periods) {
        return (int) periods.stream()
                .filter(period -> {
                    LocalTime from = notBeforePerDay.get(period.getDay());
                    return from != null && period.getStartsAt().isBefore(from);
                })
                .count();
    }

    /** A teacher teaching before their time on a day: soft, the weight for each period that starts before it. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherNotBefore.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Teacher before their start time");
    }
}
