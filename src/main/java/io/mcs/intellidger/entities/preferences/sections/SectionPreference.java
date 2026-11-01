package io.mcs.intellidger.entities.preferences.sections;

import io.mcs.intellidger.entities.sections.Section;
import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
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
 * Something a section would like from its week in a term, and how much it matters. Each kind of wish is its own
 * class; a timetable pays the weight for each period or day it misses the wish by.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "preference_kind", discriminatorType = DiscriminatorType.STRING)
@Table(name = "section_preferences")
@Getter
@Setter
public abstract class SectionPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "section_id", nullable = false, updatable = false)
    private Section section;

    /** How much it matters: what a timetable pays for each period or day it misses the wish by. */
    @NotNull
    @Positive
    @Column(name = "weight", nullable = false)
    private Integer weight;
}
