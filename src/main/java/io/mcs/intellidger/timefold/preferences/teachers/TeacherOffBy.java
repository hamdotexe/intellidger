package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherOffByRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the teacher is done by a time on each day listed. */
@Getter
public class TeacherOffBy extends TeacherPreference {

    private final Map<DayOfWeek, LocalTime> offByPerDay;

    public TeacherOffBy(Teacher teacher, int weight, Map<DayOfWeek, LocalTime> offByPerDay) {
        super(teacher, weight);
        this.offByPerDay = offByPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherOffBy> fetch(TeacherOffByRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherOffBy(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), Map.copyOf(preference.getOffByPerDay())))
                .toList();
    }
}
