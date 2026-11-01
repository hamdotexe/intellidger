package io.mcs.intellidger.entities.preferences.teachers;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.DayOfWeek;
import java.util.EnumMap;
import java.util.Map;

/**
 * That the teacher teaches at least so many periods on some days, each day its own number: a timetable pays the weight
 * for each period a day falls short. A day not listed has no minimum.
 */
@Entity
@Audited
@DiscriminatorValue("MIN_DAILY_LOAD")
@Table(name = "teacher_min_daily_loads")
@Getter
@Setter
public class TeacherMinDailyLoad extends TeacherPreference {

    /** The fewest periods they want to teach on each day it lists. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "teacher_min_daily_load_days", joinColumns = @JoinColumn(name = "preference_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day")
    @Column(name = "min_periods", nullable = false)
    private Map<DayOfWeek, @NotNull @Positive Integer> minPeriodsPerDay = new EnumMap<>(DayOfWeek.class);
}
