package io.mcs.intellidger.entities.terms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * One semester of the department, such as Fall 2026. What changes from semester to semester belongs to a term: its
 * week plan, everyone's blocks and loads, what each section is taught, and its timetables. Teachers, rooms, sections
 * and courses outlive it.
 */
@Entity
@Audited
@Table(name = "terms", uniqueConstraints = @UniqueConstraint(name = "uk_terms_name", columnNames = "name"))
@Getter
@Setter
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
    @Version
    @Column(name = "jpa_version", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long jpaVersion;

    /** As the department names it, such as "Fall 2026". */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
