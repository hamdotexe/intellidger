package io.mcs.intellidger.entities.sections;

import io.mcs.intellidger.entities.terms.Term;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * One group of an elective for a batch in a term, such as IoT-1 of BESE-30's two IoT groups. Its students come from
 * every regular section of the batch, so while it meets, none of those sections can. Its offerings are the elective's
 * theory, and its lab if it has one.
 */
@Entity
@Audited
@DiscriminatorValue("ELECTIVE")
@Table(name = "elective_sections", uniqueConstraints = @UniqueConstraint(name = "uk_elective_sections_term_name",
        columnNames = {"term_id", "name"}))
@Getter
@Setter
public class ElectiveSection extends Section {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "term_id", nullable = false, updatable = false)
    private Term term;

    /** As timetables print it, such as "IoT-1". No two elective sections of a term share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
