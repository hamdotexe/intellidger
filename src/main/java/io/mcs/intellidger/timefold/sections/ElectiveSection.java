package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.repositories.sections.ElectiveSectionRepository;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/**
 * One group of an elective, such as IoT-1, drawing students from every regular section of its batch. Which elective it
 * is, is its offerings' course.
 */
public class ElectiveSection extends Section {

    public ElectiveSection(UUID id, String name, Batch batch) {
        super(id, name, batch);
    }

    /** The term's elective sections, as saved. */
    public static List<ElectiveSection> fetch(ElectiveSectionRepository sections, UUID termId, Timetable timetable) {
        return sections.findByTermId(termId).stream()
                .map(section -> new ElectiveSection(section.getId(), section.getName(),
                        timetable.batch(section.getBatch().getId())))
                .toList();
    }
}
