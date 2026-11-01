package io.mcs.intellidger.timefold.rooms;

import io.mcs.intellidger.timefold.weekplan.Block;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

/** A period a room is blocked on, hard or soft. */
@Getter
public abstract class RoomBlock extends Block {

    private final Room room;

    protected RoomBlock(Period period, Room room) {
        super(period);
        this.room = room;
    }
}
