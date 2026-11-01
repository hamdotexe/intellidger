package io.mcs.intellidger.timefold.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
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

    /**
     * A section at a session in its hard block, a regular section's keeping its batch's elective sections out too:
     * hard.
     */
    public static Constraint used(ConstraintFactory factory) {
        return Section.sessionsAttended(factory)
                .join(SectionHardBlock.class,
                        Joiners.equal((section, session) -> section, block -> block.getSection()),
                        Joiners.filtering((section, session, block) -> session.covers(block.getPeriod())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Section at a session in its hard block");
    }
}
