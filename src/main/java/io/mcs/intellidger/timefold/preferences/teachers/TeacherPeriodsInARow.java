package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherPeriodsInARowRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
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
}
