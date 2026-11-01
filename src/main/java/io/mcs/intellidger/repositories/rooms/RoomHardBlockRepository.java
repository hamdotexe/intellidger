package io.mcs.intellidger.repositories.rooms;

import io.mcs.intellidger.entities.rooms.RoomHardBlock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/** Rooms' hard blocks in a term, found through their periods. */
public interface RoomHardBlockRepository extends JpaRepository<RoomHardBlock, UUID> {

    List<RoomHardBlock> findByPeriodTermId(UUID termId);
}
