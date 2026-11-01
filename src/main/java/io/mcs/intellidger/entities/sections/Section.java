package io.mcs.intellidger.entities.sections;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * A section of students taught together, such as BESE-30-C. Kept across terms; what it is taught in a term is its
 * offerings.
 */
@Entity
@Audited
@Table(name = "sections", uniqueConstraints = @UniqueConstraint(name = "uk_sections_name", columnNames = "name"))
@Getter
@Setter
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** As timetables print it, such as "BESE-30-C". No two sections share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
