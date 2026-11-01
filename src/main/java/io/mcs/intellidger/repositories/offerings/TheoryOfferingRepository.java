package io.mcs.intellidger.repositories.offerings;

import io.mcs.intellidger.entities.offerings.TheoryOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** A term's theory offerings. */
public interface TheoryOfferingRepository extends JpaRepository<TheoryOffering, UUID> {

    List<TheoryOffering> findByTermId(UUID termId);
}
