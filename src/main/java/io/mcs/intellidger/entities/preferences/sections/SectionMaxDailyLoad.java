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
 * That the section has at most so many periods on some days, each day its own number, such as at most 4 on Fridays: a
 * timetable pays the weight for each period a day goes over. A day not listed has no maximum.
 */
@Entity
@Audited
@DiscriminatorValue("MAX_DAILY_LOAD")
@Table(name = "section_max_daily_loads")
@Getter
@Setter
public class SectionMaxDailyLoad extends SectionPreference {

    /** The most periods it wants on each day it lists. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "section_max_daily_load_days", joinColumns = @JoinColumn(name = "preference_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day")
    @Column(name = "max_periods", nullable = false)
    private Map<DayOfWeek, @NotNull @Positive Integer> maxPeriodsPerDay = new EnumMap<>(DayOfWeek.class);
}
