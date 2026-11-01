package io.mcs.intellidger.timefold.courses;

import io.mcs.intellidger.repositories.courses.CourseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A course, such as Artificial Intelligence: the same course for its theory and its lab. */
@Getter
@AllArgsConstructor
public class Course {

    private final UUID id;

    private final String name;

    /** Every course, as saved: they outlive terms. */
    public static List<Course> fetch(CourseRepository courses) {
        return courses.findAll().stream()
                .map(course -> new Course(course.getId(), course.getName()))
                .toList();
    }
}
