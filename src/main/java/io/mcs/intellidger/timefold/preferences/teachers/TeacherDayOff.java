package io.mcs.intellidger.timefold.preferences.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.teachers.TeacherDayOffRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the teacher has a given day with no sessions. */
@Getter
public class TeacherDayOff extends TeacherPreference {

    private final DayOfWeek day;

    public TeacherDayOff(Teacher teacher, int weight, DayOfWeek day) {
        super(teacher, weight);
        this.day = day;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherDayOff> fetch(TeacherDayOffRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherDayOff(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getDay()))
                .toList();
    }

    /** How many of these periods fall on its day. */
    public int missedBy(Set<Period> periods) {
        return Period.countOn(periods, day);
    }

    /** A teacher with sessions on their day off: soft, the weight for each period that day. */
    public static Constraint missed(ConstraintFactory factory) {
        return Teacher.periodsAttended(factory)
                .join(TeacherDayOff.class,
                        Joiners.equal((teacher, periods) -> teacher, preference -> preference.getTeacher()))
                .filter((teacher, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (teacher, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Teacher on their day off");
    }
}
