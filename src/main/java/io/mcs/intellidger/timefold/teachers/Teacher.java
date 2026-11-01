package io.mcs.intellidger.timefold.teachers;

import io.mcs.intellidger.repositories.teachers.TeacherRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** Someone who teaches, a lecturer or a lab engineer alike: what an instructor or lab engineer assignment is given. */
@Getter
@AllArgsConstructor
public class Teacher {

    private final UUID id;

    private final String name;

    /** Every teacher, as saved: they outlive terms. */
    public static List<Teacher> fetch(TeacherRepository teachers) {
        return teachers.findAll().stream()
                .map(teacher -> new Teacher(teacher.getId(), teacher.getName()))
                .toList();
    }
}
