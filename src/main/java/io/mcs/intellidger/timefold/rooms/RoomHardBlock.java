package io.mcs.intellidger.timefold.rooms;

import io.mcs.intellidger.repositories.rooms.RoomHardBlockRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.util.List;
import java.util.UUID;

/** A period a room is never used in. */
public class RoomHardBlock extends RoomBlock {

    public RoomHardBlock(Period period, Room room) {
        super(period, room);
    }

    /** The term's blocks of this kind, as saved. */
    public static List<RoomHardBlock> fetch(RoomHardBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new RoomHardBlock(timetable.period(block.getPeriod().getId()),
                        timetable.room(block.getRoom().getId())))
                .toList();
    }
}
