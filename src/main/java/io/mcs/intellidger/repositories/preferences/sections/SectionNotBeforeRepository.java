package io.mcs.intellidger.repositories.preferences.sections;

import io.mcs.intellidger.entities.preferences.sections.SectionNotBefore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Sections' wishes of this kind in a term. */
public interface SectionNotBeforeRepository extends JpaRepository<SectionNotBefore, UUID> {

    List<SectionNotBefore> findByTermId(UUID termId);
}
