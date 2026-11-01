package io.mcs.intellidger.entities.offerings;

import io.mcs.intellidger.entities.rooms.Room;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A room an offering would like, and how much: 1 first, equal numbers equally. */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RoomPriority {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @NotNull
    @Positive
    @Column(name = "priority", nullable = false)
    private Integer priority;
}
