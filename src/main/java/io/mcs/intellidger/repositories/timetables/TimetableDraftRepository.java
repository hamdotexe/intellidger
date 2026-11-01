package io.mcs.intellidger.repositories.timetables;

import io.mcs.intellidger.entities.timetables.TimetableDraft;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/** A term's draft, the timetable being worked on. */
public interface TimetableDraftRepository extends JpaRepository<TimetableDraft, UUID> {

    Optional<TimetableDraft> findByTermId(UUID termId);
}
