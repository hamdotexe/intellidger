package io.mcs.intellidger.repositories.preferences.teachers;

import io.mcs.intellidger.entities.preferences.teachers.TeacherNotBefore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' wishes of this kind in a term. */
public interface TeacherNotBeforeRepository extends JpaRepository<TeacherNotBefore, UUID> {

    List<TeacherNotBefore> findByTermId(UUID termId);
}
