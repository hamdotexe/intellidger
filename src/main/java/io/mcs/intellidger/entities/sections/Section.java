package io.mcs.intellidger.entities.sections;

import io.mcs.intellidger.entities.batches.Batch;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * Students taught together: a batch's regular section, such as BESE-30-C, or an elective section drawn from the whole
 * batch, such as IoT-1. What a section is taught in a term is its offerings.
 */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "section_kind", discriminatorType = DiscriminatorType.STRING)
@Table(name = "sections")
@Getter
@Setter
public abstract class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** The batch it is part of: a regular section's own, or the one an elective section draws its students from. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "batch_id", nullable = false, updatable = false)
    private Batch batch;

    /** As timetables print it, such as "BESE-30-C" or "IoT-1". */
    public abstract String getName();
}
