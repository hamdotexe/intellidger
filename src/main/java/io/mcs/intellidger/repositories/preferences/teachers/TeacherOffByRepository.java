package io.mcs.intellidger.repositories.preferences.teachers;

import io.mcs.intellidger.entities.preferences.teachers.TeacherOffBy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' wishes of this kind in a term. */
public interface TeacherOffByRepository extends JpaRepository<TeacherOffBy, UUID> {

    List<TeacherOffBy> findByTermId(UUID termId);
}
