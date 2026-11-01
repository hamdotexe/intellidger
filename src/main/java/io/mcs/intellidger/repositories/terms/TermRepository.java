package io.mcs.intellidger.repositories.terms;

import io.mcs.intellidger.entities.terms.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Terms, each found by its id. */
public interface TermRepository extends JpaRepository<Term, UUID> {
}
