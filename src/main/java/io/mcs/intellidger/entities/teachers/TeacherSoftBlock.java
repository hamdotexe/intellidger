package io.mcs.intellidger.entities.teachers;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/** A period a teacher would rather not teach: a session of theirs may use it, at the cost of its weight. */
@Entity
@Audited
@DiscriminatorValue("SOFT")
@Table(name = "teacher_soft_blocks")
@Getter
@Setter
public class TeacherSoftBlock extends TeacherBlock {

    /** How much it matters: what a timetable pays when a session uses the period anyway. */
    @NotNull
    @Positive
    @Column(name = "weight", nullable = false)
    private Integer weight;
}
