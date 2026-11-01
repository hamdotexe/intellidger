package io.mcs.intellidger.entities.offerings;

import io.mcs.intellidger.entities.teachers.Teacher;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A teacher who may teach an offering, or be a lab offering's lab engineer, and how much they are wanted: 1 first,
 * equal numbers equally. Only teachers in the list can ever be given the offering; there is no switch to allow others.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class InstructorPriority {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @NotNull
    @Positive
    @Column(name = "priority", nullable = false)
    private Integer priority;
}
