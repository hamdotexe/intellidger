package io.mcs.intellidger.repositories.rooms;

import io.mcs.intellidger.entities.rooms.RoomSoftBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Rooms' soft blocks in a term, found through their periods. */
public interface RoomSoftBlockRepository extends JpaRepository<RoomSoftBlock, UUID> {

    List<RoomSoftBlock> findByPeriodTermId(UUID termId);
}
