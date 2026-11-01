package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherNotBeforeRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher starts no earlier than a time on each day listed. */
@Getter
public class TeacherNotBefore extends TeacherPreference {

    private final Map<DayOfWeek, LocalTime> notBeforePerDay;

    public TeacherNotBefore(Teacher teacher, int weight, Map<DayOfWeek, LocalTime> notBeforePerDay) {
        super(teacher, weight);
        this.notBeforePerDay = notBeforePerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherNotBefore> fetch(TeacherNotBeforeRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherNotBefore(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getNotBeforePerDay())))
                .toList();
    }
}
