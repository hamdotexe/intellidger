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
 * That the section has some days with no sessions, whichever days they are, its load gathered into the others: a
 * timetable pays the weight for each free day short.
 */
@Entity
@Audited
@DiscriminatorValue("FREE_DAYS")
@Table(name = "section_free_days")
@Getter
@Setter
public class SectionFreeDays extends SectionPreference {

    /** How many free days it wants. */
    @NotNull
    @Positive
    @Column(name = "how_many_days", nullable = false)
    private Integer howManyDays;
}
