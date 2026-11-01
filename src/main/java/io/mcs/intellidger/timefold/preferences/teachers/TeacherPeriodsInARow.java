package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherPeriodsInARowRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the teacher teaches no more than so many periods in a row. */
@Getter
public class TeacherPeriodsInARow extends TeacherPreference {

    private final int maxPeriods;

    public TeacherPeriodsInARow(Teacher teacher, int weight, int maxPeriods) {
        super(teacher, weight);
        this.maxPeriods = maxPeriods;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherPeriodsInARow> fetch(TeacherPeriodsInARowRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherPeriodsInARow(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getMaxPeriods()))
                .toList();
    }

    /** How many of these periods come after its most in a row. */
    public int missedBy(Set<Period> periods) {
        return Period.beyondInARow(periods, maxPeriods);
    }

    /** A teacher teaching more periods in a row than they want: soft, the weight for each period beyond. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherPeriodsInARow.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Teacher too many periods in a row");
    }
}
