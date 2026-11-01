package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxWeeklyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the teacher teaches at most so many periods in the week. */
@Getter
public class TeacherMaxWeeklyLoad extends TeacherPreference {

    private final int maxPeriodsPerWeek;

    public TeacherMaxWeeklyLoad(Teacher teacher, int weight, int maxPeriodsPerWeek) {
        super(teacher, weight);
        this.maxPeriodsPerWeek = maxPeriodsPerWeek;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMaxWeeklyLoad> fetch(TeacherMaxWeeklyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMaxWeeklyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getMaxPeriodsPerWeek()))
                .toList();
    }
}
