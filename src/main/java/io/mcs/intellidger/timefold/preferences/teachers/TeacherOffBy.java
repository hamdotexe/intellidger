package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherOffByRepository;
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

/** That the teacher is done by a time on each day listed. */
@Getter
public class TeacherOffBy extends TeacherPreference {

    private final Map<DayOfWeek, LocalTime> offByPerDay;

    public TeacherOffBy(Teacher teacher, int weight, Map<DayOfWeek, LocalTime> offByPerDay) {
        super(teacher, weight);
        this.offByPerDay = offByPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherOffBy> fetch(TeacherOffByRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherOffBy(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getOffByPerDay())))
                .toList();
    }

    /** How many of these periods end after its time on their day. */
    public int missedBy(Set<Period> periods) {
        return (int) periods.stream()
                .filter(period -> {
                    LocalTime by = offByPerDay.get(period.getDay());
                    return by != null && period.getEndsAt().isAfter(by);
                })
                .count();
    }

    /** A teacher teaching after their time on a day: soft, the weight for each period that ends after it. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherOffBy.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Teacher after their finish time");
    }
}
