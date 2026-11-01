package io.mcs.intellidger.entities.sections;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/** One of a batch's sections, such as BESE-30-C: the same students, taught together term after term. */
@Entity
@Audited
@DiscriminatorValue("REGULAR")
@Table(name = "regular_sections", uniqueConstraints = @UniqueConstraint(name = "uk_regular_sections_name",
        columnNames = "name"))
@Getter
@Setter
public class RegularSection extends Section {

    /** As timetables print it, such as "BESE-30-C". No two regular sections share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
