package io.mcs.intellidger.shared.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.Getter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * What every entity of ours has: an id, and an optimistic lock, so two windows saving the same row at once get a
 * conflict for the second rather than a silent overwrite of the first.
 *
 * <p>Audited so an audited entity's history carries these too; whether an entity is audited at all is still its own
 * {@code @Audited}.
 */
@MappedSuperclass
@Audited
@Getter
public abstract class BaseEntity {

    /** Kept in SQLite as its 36-character text, so the file reads plainly in any SQLite browser. */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Null on a new row on purpose: Spring Data takes a set version to mean "already saved". */
    @Version
    @Column(name = "jpa_version", nullable = false)
    private Long jpaVersion;
}
