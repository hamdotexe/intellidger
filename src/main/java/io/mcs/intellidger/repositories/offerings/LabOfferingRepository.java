package io.mcs.intellidger.repositories.offerings;

import io.mcs.intellidger.entities.offerings.LabOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** A term's lab offerings. */
public interface LabOfferingRepository extends JpaRepository<LabOffering, UUID> {

    List<LabOffering> findByTermId(UUID termId);
}
