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
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumMap;
import java.util.Map;

/**
 * That the section is done by a time on some days, each day its own time, such as by 13:00 on Thursdays and 15:00 on
 * Mondays: a timetable pays the weight for each period of a session that ends after it.
 */
@Entity
@Audited
@DiscriminatorValue("OFF_BY")
@Table(name = "section_off_by")
@Getter
@Setter
public class SectionOffBy extends SectionPreference {

    /** The time it wants to be done by, on each day it lists. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "section_off_by_days", joinColumns = @JoinColumn(name = "preference_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day")
    @Column(name = "off_by", nullable = false)
    private Map<DayOfWeek, @NotNull LocalTime> offByPerDay = new EnumMap<>(DayOfWeek.class);
}
