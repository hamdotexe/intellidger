package io.mcs.intellidger.entities.weekplan;

import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

/**
 * One period of a term's week: the "1st" on Monday from 08:00 to 08:50. A day with no periods is not taught. A session
 * starts on a period and takes the next ones of the same day; a break between two periods doesn't part them, only a
 * period in between does.
 */
@Entity
@Audited
@Table(name = "periods", uniqueConstraints = @UniqueConstraint(name = "uk_periods_term_day_number",
        columnNames = {"term_id", "day", "number"}))
@Getter
@Setter
public class Period {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private DayOfWeek day;

    /** Its place in the day, from 1. Two periods with numbers next to each other are consecutive. */
    @NotNull
    @Positive
    @Column(name = "number", nullable = false)
    private Integer number;

    @NotNull
    @Column(name = "starts_at", nullable = false)
    private LocalTime startsAt;

    @NotNull
    @Column(name = "ends_at", nullable = false)
    private LocalTime endsAt;

    @AssertTrue(message = "A period must end after it starts.")
    public boolean isInOrder() {
        return startsAt == null || endsAt == null || startsAt.isBefore(endsAt);
    }
}
