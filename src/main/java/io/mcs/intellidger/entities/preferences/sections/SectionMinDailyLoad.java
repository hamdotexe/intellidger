package io.mcs.intellidger.entities.preferences.sections;

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
 * That the section has at least so many periods on some days, each day its own number, such as at least 3 from Monday
 * to Thursday: a timetable pays the weight for each period a day falls short. A day not listed has no minimum.
 */
@Entity
@Audited
@DiscriminatorValue("MIN_DAILY_LOAD")
@Table(name = "section_min_daily_loads")
@Getter
@Setter
public class SectionMinDailyLoad extends SectionPreference {

    /** The fewest periods it wants on each day it lists. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "section_min_daily_load_days", joinColumns = @JoinColumn(name = "preference_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day")
    @Column(name = "min_periods", nullable = false)
    private Map<DayOfWeek, @NotNull @Positive Integer> minPeriodsPerDay = new EnumMap<>(DayOfWeek.class);
}
