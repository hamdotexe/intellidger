package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherMinDailyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher teaches at least so many periods on each day listed. */
@Getter
public class TeacherMinDailyLoad extends TeacherPreference {

    private final Map<DayOfWeek, Integer> minPeriodsPerDay;

    public TeacherMinDailyLoad(Teacher teacher, int weight, Map<DayOfWeek, Integer> minPeriodsPerDay) {
        super(teacher, weight);
        this.minPeriodsPerDay = minPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMinDailyLoad> fetch(TeacherMinDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMinDailyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMinPeriodsPerDay())))
                .toList();
    }
}
