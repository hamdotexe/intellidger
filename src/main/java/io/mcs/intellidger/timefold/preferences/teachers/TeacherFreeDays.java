package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherFreeDaysRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the teacher has so many days with no sessions, whichever days they are. */
@Getter
public class TeacherFreeDays extends TeacherPreference {

    private final int howManyDays;

    public TeacherFreeDays(Teacher teacher, int weight, int howManyDays) {
        super(teacher, weight);
        this.howManyDays = howManyDays;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherFreeDays> fetch(TeacherFreeDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherFreeDays(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getHowManyDays()))
                .toList();
    }

    /** How many free days these periods leave it short of, among the teaching days. */
    public int missedBy(Set<Period> periods, Set<DayOfWeek> teachingDays) {
        return Math.max(0, howManyDays - (teachingDays.size() - Period.daysWith(periods)));
    }

    /** A teacher with fewer free days than they want: soft, the weight for each day short. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherFreeDays.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .join(Period.teachingDays(factory))
                .filter((teacher, periods, preference, days) -> preference.missedBy(periods, days) > 0)
                .penalize(HardSoftScore.ONE_SOFT, (teacher, periods, preference, days) ->
                        preference.getWeight() * preference.missedBy(periods, days))
                .asConstraint("Teacher short of their free days");
    }
}
