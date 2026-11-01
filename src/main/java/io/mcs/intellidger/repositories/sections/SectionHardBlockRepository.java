package io.mcs.intellidger.repositories.sections;

import io.mcs.intellidger.entities.sections.SectionHardBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' hard blocks in a term, found through their periods. */
public interface SectionHardBlockRepository extends JpaRepository<SectionHardBlock, UUID> {

    List<SectionHardBlock> findByPeriodTermId(UUID termId);
}
