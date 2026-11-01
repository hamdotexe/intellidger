package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherEvenDaysRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the teacher's load is spread evenly over every teaching day of the week. */
public class TeacherEvenDays extends TeacherPreference {

    public TeacherEvenDays(Teacher teacher, int weight) {
        super(teacher, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherEvenDays> fetch(TeacherEvenDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherEvenDays(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight()))
                .toList();
    }
}
