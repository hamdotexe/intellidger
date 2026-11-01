package io.mcs.intellidger.repositories.weekplan;

import io.mcs.intellidger.entities.weekplan.Period;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** A term's periods. */
public interface PeriodRepository extends JpaRepository<Period, UUID> {

    List<Period> findByTermId(UUID termId);
}
