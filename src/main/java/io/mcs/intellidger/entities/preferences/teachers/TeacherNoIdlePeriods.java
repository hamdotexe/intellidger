package io.mcs.intellidger.entities.preferences.teachers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/**
 * That the teacher's days have no free periods between their first session and their last: a timetable pays the
 * weight for each such idle period.
 */
@Entity
@Audited
@DiscriminatorValue("NO_IDLE_PERIODS")
@Table(name = "teacher_no_idle_periods")
public class TeacherNoIdlePeriods extends TeacherPreference {
}
