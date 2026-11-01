package io.mcs.intellidger.timefold.offerings;

import io.mcs.intellidger.timefold.rooms.Room;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** A room an offering would like: 1 first, equal numbers equally. */
@Getter
@AllArgsConstructor
public class RoomPriority {

    private final Room room;

    private final int priority;
}
