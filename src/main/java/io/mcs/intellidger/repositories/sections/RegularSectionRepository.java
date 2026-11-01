package io.mcs.intellidger.repositories.sections;

import io.mcs.intellidger.entities.sections.RegularSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Every regular section; they outlive terms. */
public interface RegularSectionRepository extends JpaRepository<RegularSection, UUID> {
}
