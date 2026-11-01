package io.mcs.intellidger.entities.batches;

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
 * Students who entered together, such as BESE-30, taught in regular sections (BESE-30-A, -B, -C) and, for electives,
 * in elective sections drawn from all of them. Kept across terms.
 */
@Entity
@Audited
@Table(name = "batches", uniqueConstraints = @UniqueConstraint(name = "uk_batches_name", columnNames = "name"))
@Getter
@Setter
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** As the department names it, such as "BESE-30". No two batches share one. */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
