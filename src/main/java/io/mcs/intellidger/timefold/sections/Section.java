package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.timefold.batches.Batch;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/** Students taught together: a batch's regular section, or an elective section drawn from the whole batch. */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Section {

    private final UUID id;

    /** As timetables print it, such as "BESE-30-C" or "IoT-1". */
    private final String name;

    /** The batch it is part of: a regular section's own, or the one an elective section draws its students from. */
    private final Batch batch;
}
