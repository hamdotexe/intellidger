package io.mcs.intellidger.entities.rooms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/** A room or a lab sessions are held in, such as CR-31 or the Programming Lab. Kept across terms. */
@Entity
@Audited
@Table(name = "rooms", uniqueConstraints = @UniqueConstraint(name = "uk_rooms_name", columnNames = "name"))
@Getter
@Setter
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** As timetables print it, such as "CR-215 IS Dept". No two rooms share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    /** How many sections it can take in the same period: 1 for nearly every room, 2 or 3 for a few. */
    @NotNull
    @Positive
    @Column(name = "max_sections_at_once", nullable = false)
    private Integer maxSectionsAtOnce = 1;
}
