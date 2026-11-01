package io.mcs.intellidger.timefold.rooms;

import io.mcs.intellidger.repositories.rooms.RoomRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A room or a lab, and how many sections it can take in the same period. */
@Getter
@AllArgsConstructor
public class Room {

    private final UUID id;

    private final String name;

    private final int maxSectionsAtOnce;

    /** Every room, as saved: they outlive terms. */
    public static List<Room> fetch(RoomRepository rooms) {
        return rooms.findAll().stream()
                .map(room -> new Room(room.getId(), room.getName(), room.getMaxSectionsAtOnce()))
                .toList();
    }
}
