package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.repositories.preferences.teachers.TeacherFreeDaysRepository;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the teacher has so many days with no sessions, whichever days they are. */
@Getter
public class TeacherFreeDays extends TeacherPreference {

    private final int howManyDays;

    public TeacherFreeDays(Teacher teacher, int weight, int howManyDays) {
        super(teacher, weight);
        this.howManyDays = howManyDays;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<TeacherFreeDays> fetch(TeacherFreeDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new TeacherFreeDays(timetable.teacher(preference.getTeacher().getId()),
                        preference.getWeight(), preference.getHowManyDays()))
                .toList();
    }
}
