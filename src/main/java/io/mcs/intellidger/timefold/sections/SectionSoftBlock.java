package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.repositories.sections.SectionSoftBlockRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A period a section would rather not be used in: a session may use it, at the cost of its weight. */
@Getter
public class SectionSoftBlock extends SectionBlock {

    /** What a timetable pays when a session uses the period anyway. */
    private final int weight;

    public SectionSoftBlock(Period period, Section section, int weight) {
        super(period, section);
        this.weight = weight;
    }

    /** The term's blocks of this kind, as saved. */
    public static List<SectionSoftBlock> fetch(SectionSoftBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new SectionSoftBlock(timetable.period(block.getPeriod().getId()),
                        timetable.section(block.getSection().getId()), block.getWeight()))
                .toList();
    }
}
