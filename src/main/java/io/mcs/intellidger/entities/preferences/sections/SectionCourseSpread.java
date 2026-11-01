package io.mcs.intellidger.entities.preferences.sections;

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
 * That two sessions of one course fall some days apart, such as Tuesday and Thursday rather than Tuesday and
 * Wednesday: a timetable pays the weight for each day two sessions fall short.
 */
@Entity
@Audited
@DiscriminatorValue("COURSE_SPREAD")
@Table(name = "section_course_spreads")
@Getter
@Setter
public class SectionCourseSpread extends SectionPreference {

    /** Days wanted between two sessions of one course: 1 keeps them off neighbouring days. */
    @NotNull
    @Positive
    @Column(name = "min_days_between", nullable = false)
    private Integer minDaysBetween;
}
