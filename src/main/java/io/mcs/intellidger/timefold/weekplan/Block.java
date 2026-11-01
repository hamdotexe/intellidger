package io.mcs.intellidger.timefold.weekplan;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * One period marked on the week of a teacher, a room or a section. Hard, it is never used; soft, it is used only at
 * the cost the block names.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Block {

    private final Period period;
}
