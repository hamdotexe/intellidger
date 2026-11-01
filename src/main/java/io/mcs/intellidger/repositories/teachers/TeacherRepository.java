package io.mcs.intellidger.repositories.teachers;

import io.mcs.intellidger.entities.teachers.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Every teacher; they outlive terms. */
public interface TeacherRepository extends JpaRepository<Teacher, UUID> {
}
