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
 * That the section starts no earlier than a time on some days, each day its own time, such as not before 09:00 on
 * Mondays: a timetable pays the weight for each period of a session that starts before it.
 */
@Entity
@Audited
@DiscriminatorValue("NOT_BEFORE")
@Table(name = "section_not_before")
@Getter
@Setter
public class SectionNotBefore extends SectionPreference {

    /** The time it wants to start from, on each day it lists. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "section_not_before_days", joinColumns = @JoinColumn(name = "preference_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "day")
    @Column(name = "not_before", nullable = false)
    private Map<DayOfWeek, @NotNull LocalTime> notBeforePerDay = new EnumMap<>(DayOfWeek.class);
}
