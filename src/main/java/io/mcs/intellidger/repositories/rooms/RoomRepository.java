package io.mcs.intellidger.repositories.rooms;

import io.mcs.intellidger.entities.rooms.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/** Every room; they outlive terms. */
public interface RoomRepository extends JpaRepository<Room, UUID> {
}
