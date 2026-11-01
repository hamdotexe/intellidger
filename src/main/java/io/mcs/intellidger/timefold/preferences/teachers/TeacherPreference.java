package io.mcs.intellidger.timefold.preferences.teachers;

import io.mcs.intellidger.timefold.teachers.Teacher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Something a teacher would like from their week, and how much it matters. Each kind of wish is its own class. */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TeacherPreference {

    private final Teacher teacher;

    /** What a timetable pays for each period or day it misses the wish by. */
    private final int weight;
}
