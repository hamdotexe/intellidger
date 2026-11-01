package io.mcs.intellidger.timefold.offerings;

import io.mcs.intellidger.timefold.teachers.Teacher;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** A teacher who may teach an offering, or be its lab engineer: 1 first, equal numbers equally. */
@Getter
@AllArgsConstructor
public class InstructorPriority {

    private final Teacher teacher;

    private final int priority;
}
