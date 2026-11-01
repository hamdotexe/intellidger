package io.mcs.intellidger.entities.sections;

import io.mcs.intellidger.entities.weekplan.Block;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/** A period a section is blocked on in a term, hard or soft; at most one per section and period. */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "block_kind", discriminatorType = DiscriminatorType.STRING)
@Table(name = "section_blocks", uniqueConstraints = @UniqueConstraint(name = "uk_section_blocks_section_period",
        columnNames = {"section_id", "period_id"}))
@Getter
@Setter
public abstract class SectionBlock extends Block {

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
    @JoinColumn(name = "section_id", nullable = false, updatable = false)
    private Section section;
}
