package io.mcs.intellidger.repositories.terms;

import io.mcs.intellidger.entities.terms.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Terms, each found by its id, and whether a name is taken. */
public interface TermRepository extends JpaRepository<Term, UUID> {

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, UUID id);
}
