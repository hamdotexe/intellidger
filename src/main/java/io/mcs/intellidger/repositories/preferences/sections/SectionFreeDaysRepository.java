package io.mcs.intellidger.repositories.preferences.sections;

import io.mcs.intellidger.entities.preferences.sections.SectionFreeDays;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' wishes of this kind in a term. */
public interface SectionFreeDaysRepository extends JpaRepository<SectionFreeDays, UUID> {

    List<SectionFreeDays> findByTermId(UUID termId);
}
