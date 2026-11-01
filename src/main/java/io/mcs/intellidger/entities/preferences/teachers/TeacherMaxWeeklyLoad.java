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
 * That the teacher teaches at most so many periods in the week, such as at most 12: a timetable pays the weight for
 * each period over. It only matters below their teaching load's maximum per week, which no timetable ever passes.
 */
@Entity
@Audited
@DiscriminatorValue("MAX_WEEKLY_LOAD")
@Table(name = "teacher_max_weekly_loads")
@Getter
@Setter
public class TeacherMaxWeeklyLoad extends TeacherPreference {

    /** The most periods they want to teach in the week. */
    @NotNull
    @Positive
    @Column(name = "max_periods_per_week", nullable = false)
    private Integer maxPeriodsPerWeek;
}
