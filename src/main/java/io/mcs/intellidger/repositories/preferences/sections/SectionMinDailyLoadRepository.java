package io.mcs.intellidger.repositories.preferences.sections;

import io.mcs.intellidger.entities.preferences.sections.SectionMinDailyLoad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' wishes of this kind in a term. */
public interface SectionMinDailyLoadRepository extends JpaRepository<SectionMinDailyLoad, UUID> {

    List<SectionMinDailyLoad> findByTermId(UUID termId);
}
