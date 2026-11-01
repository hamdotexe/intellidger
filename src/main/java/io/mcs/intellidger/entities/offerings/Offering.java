package io.mcs.intellidger.entities.offerings;

import io.mcs.intellidger.entities.courses.Course;
import io.mcs.intellidger.entities.rooms.Room;
import io.mcs.intellidger.entities.sections.Section;
import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A course's theory or its lab taught to a section in a term, such as the theory of Artificial Intelligence for
 * BESE-30-C in Fall 2026: its sessions, who may teach it and where. One instructor teaches every session of it, chosen
 * from its instructors by priority; each session gets a room it may use. A section's offerings are what is planned for
 * it that term, and every one of them is scheduled.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "offering_kind", discriminatorType = DiscriminatorType.STRING)
@Table(name = "offerings", uniqueConstraints = @UniqueConstraint(name = "uk_offerings_term_course_section_kind",
        columnNames = {"term_id", "course_id", "section_id", "offering_kind"}))
@Getter
@Setter
public abstract class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false, updatable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false, updatable = false)
    private Section section;

    /**
     * How long each of its sessions is, in periods, exactly as entered: 2 and 1 for a 2+1, one 3 for a lab. Never split
     * or merged: a 2+1 is never 1+1+1 or 3, a 2 never 1+1.
     */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "offering_session_lengths", joinColumns = @JoinColumn(name = "offering_id"))
    @OrderColumn(name = "position")
    @Column(name = "length_in_periods", nullable = false)
    private List<@Positive Integer> sessionLengthsInPeriods = new ArrayList<>();

    /**
     * Who may teach it, by priority: 1 first, the same number for equal choices. A teacher not listed may not. Kept in
     * the order entered, which also gives each its own row in the audit history.
     */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "offering_instructors", joinColumns = @JoinColumn(name = "offering_id"))
    @OrderColumn(name = "position")
    private List<@Valid InstructorPriority> instructors = new ArrayList<>();

    /** The rooms it would like, by priority as for instructors. A room not listed comes after every listed one. */
    @ElementCollection
    @CollectionTable(name = "offering_rooms", joinColumns = @JoinColumn(name = "offering_id"))
    @OrderColumn(name = "position")
    private List<@Valid RoomPriority> rooms = new ArrayList<>();

    /** Rooms it may never use. */
    @ManyToMany
    @JoinTable(name = "offering_impossible_rooms", joinColumns = @JoinColumn(name = "offering_id"),
            inverseJoinColumns = @JoinColumn(name = "room_id"))
    private Set<Room> impossibleRooms = new HashSet<>();

    /** "Only these rooms": no room but the listed ones, as a lab needs. Off, any room not impossible will do. */
    @Column(name = "only_listed_rooms", nullable = false)
    private boolean onlyListedRooms;

    @AssertTrue(message = "With only these rooms, at least one room must be listed.")
    public boolean isRoomPossible() {
        return !onlyListedRooms || !rooms.isEmpty();
    }
}
