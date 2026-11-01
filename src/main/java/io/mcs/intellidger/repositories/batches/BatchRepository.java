package io.mcs.intellidger.repositories.batches;

import io.mcs.intellidger.entities.batches.Batch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Every batch; they outlive terms. */
public interface BatchRepository extends JpaRepository<Batch, UUID> {
}
