package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherNoIdlePeriodsRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the teacher's days have no free periods between their first session and their last. */
public class TeacherNoIdlePeriods extends TeacherPreference {

    public TeacherNoIdlePeriods(Teacher teacher, int weight) {
        super(teacher, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherNoIdlePeriods> fetch(TeacherNoIdlePeriodsRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherNoIdlePeriods(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight()))
                .toList();
    }
}
