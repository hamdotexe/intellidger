package io.mcs.intellidger.entities.teachers;

import io.mcs.intellidger.entities.weekplan.Block;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/** A period a teacher is blocked on in a term, hard or soft; at most one per teacher and period. */
@Entity
@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "block_kind", discriminatorType = DiscriminatorType.STRING)
@Table(name = "teacher_blocks", uniqueConstraints = @UniqueConstraint(name = "uk_teacher_blocks_teacher_period",
        columnNames = {"teacher_id", "period_id"}))
@Getter
@Setter
public abstract class TeacherBlock extends Block {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, updatable = false)
    private Teacher teacher;
}
