package io.mcs.intellidger.repositories.sections;

import io.mcs.intellidger.entities.sections.ElectiveSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** A term's elective sections. */
public interface ElectiveSectionRepository extends JpaRepository<ElectiveSection, UUID> {

    List<ElectiveSection> findByTermId(UUID termId);
}
