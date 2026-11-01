package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.repositories.sections.RegularSectionRepository;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** One of a batch's sections, such as BESE-30-C. */
public class RegularSection extends Section {

    public RegularSection(UUID id, String name, Batch batch) {
        super(id, name, batch);
    }

    /** Every regular section, as saved: they outlive terms. */
    public static List<RegularSection> fetch(RegularSectionRepository sections, Timetable timetable) {
        return sections.findAll().stream()
                .map(section -> new RegularSection(section.getId(), section.getName(),
                        timetable.batch(section.getBatch().getId())))
                .toList();
    }
}
