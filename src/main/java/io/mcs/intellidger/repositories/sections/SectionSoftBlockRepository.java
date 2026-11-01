package io.mcs.intellidger.repositories.sections;

import io.mcs.intellidger.entities.sections.SectionSoftBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' soft blocks in a term, found through their periods. */
public interface SectionSoftBlockRepository extends JpaRepository<SectionSoftBlock, UUID> {

    List<SectionSoftBlock> findByPeriodTermId(UUID termId);
}
