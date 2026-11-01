package io.mcs.intellidger.repositories.preferences.sections;

import io.mcs.intellidger.entities.preferences.sections.SectionPeriodsInARow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' wishes of this kind in a term. */
public interface SectionPeriodsInARowRepository extends JpaRepository<SectionPeriodsInARow, UUID> {

    List<SectionPeriodsInARow> findByTermId(UUID termId);
}
