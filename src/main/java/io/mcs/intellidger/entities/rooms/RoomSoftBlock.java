package io.mcs.intellidger.entities.rooms;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/** A period a room would rather not be used: a session may take it, at the cost of its weight. */
@Entity
@Audited
@DiscriminatorValue("SOFT")
@Table(name = "room_soft_blocks")
@Getter
@Setter
public class RoomSoftBlock extends RoomBlock {

    /** How much it matters: what a timetable pays when a session uses the period anyway. */
    @NotNull
    @Positive
    @Column(name = "weight", nullable = false)
    private Integer weight;
}
