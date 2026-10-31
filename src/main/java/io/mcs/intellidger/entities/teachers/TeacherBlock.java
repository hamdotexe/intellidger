package io.mcs.intellidger.entities.teachers;

import io.mcs.intellidger.entities.weekplan.Block;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

/** A period a teacher is blocked on in a term: hard when they can't teach then, soft when they'd rather not. */
@Entity
@Audited
@Table(name = "teacher_blocks", uniqueConstraints = @UniqueConstraint(name = "uk_teacher_blocks_teacher_period",
        columnNames = {"teacher_id", "period_id"}))
@Getter
@Setter
public class TeacherBlock extends Block {

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
    @JoinColumn(name = "teacher_id", nullable = false, updatable = false)
    private Teacher teacher;
}
