package io.mcs.intellidger.entities.preferences.teachers;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * That the teacher teaches no more than so many periods in a row: a timetable pays the weight for each period beyond.
 * Only a free period ends a run; a break between periods does not.
 */
@Entity
@Audited
@DiscriminatorValue("PERIODS_IN_A_ROW")
@Table(name = "teacher_periods_in_a_row")
@Getter
@Setter
public class TeacherPeriodsInARow extends TeacherPreference {

    /** The most periods in a row they want. */
    @NotNull
    @Positive
    @Column(name = "max_periods", nullable = false)
    private Integer maxPeriods;
}
