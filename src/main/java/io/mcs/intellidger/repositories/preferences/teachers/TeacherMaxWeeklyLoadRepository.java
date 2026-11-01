package io.mcs.intellidger.repositories.preferences.teachers;

import io.mcs.intellidger.entities.preferences.teachers.TeacherMaxWeeklyLoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' wishes of this kind in a term. */
public interface TeacherMaxWeeklyLoadRepository extends JpaRepository<TeacherMaxWeeklyLoad, UUID> {

    List<TeacherMaxWeeklyLoad> findByTermId(UUID termId);
}
