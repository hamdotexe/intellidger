package io.mcs.intellidger.entities.rooms;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/** A period a room can't be used, as when another department has it: no session may take it. */
@Entity
@Audited
@DiscriminatorValue("HARD")
@Table(name = "room_hard_blocks")
public class RoomHardBlock extends RoomBlock {
}
