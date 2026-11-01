package io.mcs.intellidger.repositories.teachers;

import io.mcs.intellidger.entities.teachers.TeacherHardBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' hard blocks in a term, found through their periods. */
public interface TeacherHardBlockRepository extends JpaRepository<TeacherHardBlock, UUID> {

    List<TeacherHardBlock> findByPeriodTermId(UUID termId);
}
