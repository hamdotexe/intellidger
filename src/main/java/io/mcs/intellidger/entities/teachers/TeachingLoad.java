package io.mcs.intellidger.entities.teachers;

import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * The most a teacher may teach in a term, in periods, a day and a week; no timetable passes either. Every period of a
 * session counts, a lab's three like any three others: always for its lab engineer, and for its instructor when the
 * term says labs count as instructor load. How much they'd like to teach is among their preferences.
 */
@Entity
@Audited
@Table(name = "teaching_loads", uniqueConstraints = @UniqueConstraint(name = "uk_teaching_loads_term_teacher",
        columnNames = {"term_id", "teacher_id"}))
@Getter
@Setter
public class TeachingLoad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, updatable = false)
    private Teacher teacher;

    @NotNull
    @PositiveOrZero
    @Column(name = "max_periods_per_day", nullable = false)
    private Integer maxPeriodsPerDay;

    @NotNull
    @PositiveOrZero
    @Column(name = "max_periods_per_week", nullable = false)
    private Integer maxPeriodsPerWeek;
}
