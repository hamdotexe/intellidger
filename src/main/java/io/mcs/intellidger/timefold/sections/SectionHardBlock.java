package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.repositories.sections.SectionHardBlockRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.util.List;
import java.util.UUID;

/** A period a section is never used in. */
public class SectionHardBlock extends SectionBlock {

    public SectionHardBlock(Period period, Section section) {
        super(period, section);
    }

    /** The term's blocks of this kind, as saved. */
    public static List<SectionHardBlock> fetch(SectionHardBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new SectionHardBlock(timetable.period(block.getPeriod().getId()),
                        timetable.section(block.getSection().getId())))
                .toList();
    }
}
