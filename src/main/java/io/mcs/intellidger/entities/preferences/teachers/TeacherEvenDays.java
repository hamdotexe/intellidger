package io.mcs.intellidger.entities.preferences.teachers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/**
 * That the teacher's load is spread evenly over every teaching day of the week, like justified text: a timetable
 * pays the weight for each period a day is away from an even share.
 */
@Entity
@Audited
@DiscriminatorValue("EVEN_DAYS")
@Table(name = "teacher_even_days")
public class TeacherEvenDays extends TeacherPreference {
}
