package io.mcs.intellidger.entities.preferences.teachers;

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
 * That the teacher has a given day with no sessions, such as Friday: a timetable pays the weight for each period they
 * teach that day. Stacks with {@link TeacherFreeDays}, each at its own weight.
 */
@Entity
@Audited
@DiscriminatorValue("DAY_OFF")
@Table(name = "teacher_days_off")
@Getter
@Setter
public class TeacherDayOff extends TeacherPreference {

    /** The day they want free. */
    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private DayOfWeek day;
}
