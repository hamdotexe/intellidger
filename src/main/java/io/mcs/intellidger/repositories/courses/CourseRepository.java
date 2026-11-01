package io.mcs.intellidger.repositories.courses;

import io.mcs.intellidger.entities.courses.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Every course; they outlive terms. */
public interface CourseRepository extends JpaRepository<Course, UUID> {
}
