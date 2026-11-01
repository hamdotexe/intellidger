package io.mcs.intellidger.entities.timetables;

import io.mcs.intellidger.entities.offerings.Offering;
import io.mcs.intellidger.entities.rooms.Room;
import io.mcs.intellidger.entities.weekplan.Period;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * One session of an offering placed in a timetable: the period it starts on, the next periods of that day for as
 * long as it lasts (breaks between them don't matter, a free period would), and its room.
 */
@Entity
@Audited
@Table(name = "sessions")
@Getter
@Setter
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timetable_id", nullable = false, updatable = false)
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false, updatable = false)
    private Offering offering;

    /** How many periods it takes: one of its offering's session lengths, never split or merged. */
    @NotNull
    @Positive
    @Column(name = "length_in_periods", nullable = false, updatable = false)
    private Integer lengthInPeriods;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "start_period_id", nullable = false)
    private Period startPeriod;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    /** Kept where it is, room and time: generating and optimizing never move it. */
    @Column(name = "locked", nullable = false)
    private boolean locked;
}
