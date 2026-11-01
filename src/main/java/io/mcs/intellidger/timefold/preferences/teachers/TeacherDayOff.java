package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherDayOffRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
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
}
