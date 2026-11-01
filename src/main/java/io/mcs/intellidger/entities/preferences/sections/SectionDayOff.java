package io.mcs.intellidger.entities.preferences.sections;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.DayOfWeek;

/**
 * That the section has a given day with no sessions, such as Friday: a timetable pays the weight for each period it
 * has that day. Stacks with {@link SectionFreeDays}, each at its own weight.
 */
@Entity
@Audited
@DiscriminatorValue("DAY_OFF")
@Table(name = "section_days_off")
@Getter
@Setter
public class SectionDayOff extends SectionPreference {

    /** The day it wants free. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private DayOfWeek day;
}
