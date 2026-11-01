package io.mcs.intellidger.repositories.timetables;

import io.mcs.intellidger.entities.timetables.TimetableVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** A term's saved versions, each found by its id. */
public interface TimetableVersionRepository extends JpaRepository<TimetableVersion, UUID> {}
