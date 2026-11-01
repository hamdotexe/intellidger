package io.mcs.intellidger.entities.courses;

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
 * A course, a subject such as Artificial Intelligence: only its name, kept across terms so it is typed once. How it is
 * taught to a section in a term is an offering.
 */
@Entity
@Audited
@Table(name = "courses", uniqueConstraints = @UniqueConstraint(name = "uk_courses_name", columnNames = "name"))
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
    @Version
    @Column(name = "jpa_version", nullable = false)
    @Setter(AccessLevel.NONE)
    private Long jpaVersion;

    /** As timetables print it, such as "Artificial Intelligence". No two courses share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
