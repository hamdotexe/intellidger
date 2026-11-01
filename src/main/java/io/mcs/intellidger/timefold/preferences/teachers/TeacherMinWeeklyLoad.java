package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinWeeklyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
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
}
