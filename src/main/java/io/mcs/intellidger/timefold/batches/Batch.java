package io.mcs.intellidger.timefold.batches;

import io.mcs.intellidger.repositories.batches.BatchRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** Students who entered together, such as BESE-30: what ties a batch's regular and elective sections together. */
@Getter
@AllArgsConstructor
public class Batch {

    private final UUID id;

    private final String name;

    /** Every batch, as saved: they outlive terms. */
    public static List<Batch> fetch(BatchRepository batches) {
        return batches.findAll().stream()
                .map(batch -> new Batch(batch.getId(), batch.getName()))
                .toList();
    }
}
