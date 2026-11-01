package io.mcs.intellidger.entities.preferences.sections;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/**
 * That the section's load is spread evenly over every teaching day of the week, like justified text: a timetable
 * pays the weight for each period a day is away from an even share.
 */
@Entity
@Audited
@DiscriminatorValue("EVEN_DAYS")
@Table(name = "section_even_days")
public class SectionEvenDays extends SectionPreference {
}
