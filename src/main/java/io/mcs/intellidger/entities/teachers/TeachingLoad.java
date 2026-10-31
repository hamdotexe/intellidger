package io.mcs.intellidger.entities.teachers;

import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * How much a teacher may teach in a term, and how much they'd like to, for a day and for a week. Every period of a
 * session counts, a lab's three like any three others, for its instructor and its lab engineer alike.
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

    /** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
    @Version
    @Column(name = "jpa_version", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long jpaVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, updatable = false)
    private Teacher teacher;

    @NotNull
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "max_per_day", nullable = false)),
            @AttributeOverride(name = "preferredMin", column = @Column(name = "preferred_min_per_day", nullable = false)),
            @AttributeOverride(name = "preferredMax", column = @Column(name = "preferred_max_per_day", nullable = false))
    })
    private LoadLimits perDay;

    @NotNull
    @Valid
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "max", column = @Column(name = "max_per_week", nullable = false)),
            @AttributeOverride(name = "preferredMin", column = @Column(name = "preferred_min_per_week",
                    nullable = false)),
            @AttributeOverride(name = "preferredMax", column = @Column(name = "preferred_max_per_week",
                    nullable = false))
    })
    private LoadLimits perWeek;
}
