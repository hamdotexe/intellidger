package io.mcs.intellidger.timefold.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
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

    /**
     * A section at a session in its soft block, a regular section's keeping its batch's elective sections out too:
     * soft, at its weight.
     */
    public static Constraint used(ConstraintFactory factory) {
        return Section.sessionsAttended(factory)
                .join(SectionSoftBlock.class,
                        Joiners.equal((section, session) -> section, block -> block.getSection()),
                        Joiners.filtering((section, session, block) -> session.covers(block.getPeriod())))
                .penalize(HardSoftScore.ONE_SOFT, (section, session, block) -> block.getWeight())
                .asConstraint("Section at a session in its soft block");
    }
}
