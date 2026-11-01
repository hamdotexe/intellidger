package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherNoIdlePeriodsRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the teacher's days have no free periods between their first session and their last. */
public class TeacherNoIdlePeriods extends TeacherPreference {

    public TeacherNoIdlePeriods(Teacher teacher, int weight) {
        super(teacher, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherNoIdlePeriods> fetch(TeacherNoIdlePeriodsRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherNoIdlePeriods(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight()))
                .toList();
    }

    /** How many free periods lie between these, on each day between its first and its last. */
    public int missedBy(Set<Period> periods) {
        return Period.idleBetween(periods);
    }

    /** A teacher with free periods inside their days: soft, the weight for each one. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherNoIdlePeriods.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Teacher idle between sessions");
    }
}
