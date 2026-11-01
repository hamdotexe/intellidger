package io.mcs.intellidger.timefold.sections;

import io.mcs.intellidger.timefold.weekplan.Block;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

/** A period a section is blocked on, hard or soft. */
@Getter
public abstract class SectionBlock extends Block {

    private final Section section;

    protected SectionBlock(Period period, Section section) {
        super(period);
        this.section = section;
    }
}
