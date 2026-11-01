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
 * That the teacher teaches at least so many periods in the week, such as at least 9: a timetable pays the weight for
 * each period short.
 */
@Entity
@Audited
@DiscriminatorValue("MIN_WEEKLY_LOAD")
@Table(name = "teacher_min_weekly_loads")
@Getter
@Setter
public class TeacherMinWeeklyLoad extends TeacherPreference {

    /** The fewest periods they want to teach in the week. */
    @NotNull
    @Positive
    @Column(name = "min_periods_per_week", nullable = false)
    private Integer minPeriodsPerWeek;
}
