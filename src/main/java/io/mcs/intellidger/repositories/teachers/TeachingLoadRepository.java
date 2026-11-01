package io.mcs.intellidger.repositories.teachers;

import io.mcs.intellidger.entities.teachers.TeachingLoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' loads in a term. */
public interface TeachingLoadRepository extends JpaRepository<TeachingLoad, UUID> {

    List<TeachingLoad> findByTermId(UUID termId);
}
