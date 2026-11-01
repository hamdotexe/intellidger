package io.mcs.intellidger.repositories.teachers;

import io.mcs.intellidger.entities.teachers.TeacherSoftBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Teachers' soft blocks in a term, found through their periods. */
public interface TeacherSoftBlockRepository extends JpaRepository<TeacherSoftBlock, UUID> {

    List<TeacherSoftBlock> findByPeriodTermId(UUID termId);
}
