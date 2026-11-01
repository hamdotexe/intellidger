package io.mcs.intellidger.entities.preferences.teachers;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * That the teacher has some days with no sessions, whichever days they are, their load gathered into the others: a
 * timetable pays the weight for each free day short.
 */
@Entity
@Audited
@DiscriminatorValue("FREE_DAYS")
@Table(name = "teacher_free_days")
@Getter
@Setter
public class TeacherFreeDays extends TeacherPreference {

    /** How many free days they want. */
    @NotNull
    @Positive
    @Column(name = "how_many_days", nullable = false)
    private Integer howManyDays;
}
