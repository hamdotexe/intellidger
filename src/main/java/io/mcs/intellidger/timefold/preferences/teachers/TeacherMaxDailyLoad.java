package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherMaxDailyLoadRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher teaches at most so many periods on each day listed. */
@Getter
public class TeacherMaxDailyLoad extends TeacherPreference {

    private final Map<DayOfWeek, Integer> maxPeriodsPerDay;

    public TeacherMaxDailyLoad(Teacher teacher, int weight, Map<DayOfWeek, Integer> maxPeriodsPerDay) {
        super(teacher, weight);
        this.maxPeriodsPerDay = maxPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherMaxDailyLoad> fetch(TeacherMaxDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherMaxDailyLoad(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMaxPeriodsPerDay())))
                .toList();
    }
}
