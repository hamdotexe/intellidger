package io.mcs.intellidger.entities.offerings;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.ArrayList;
import java.util.List;

/**
 * A course's lab for a section in a term. Its sessions need its lab engineer free for the whole session, and its
 * instructor too when the term says instructors attend labs. The lab engineer is chosen like the instructor, by
 * priority, and is the same one every week.
 */
@Entity
@Audited
@DiscriminatorValue("LAB")
@Table(name = "lab_offerings")
@Getter
@Setter
public class LabOffering extends Offering {

    /** Who may be its lab engineer, by priority: 1 first, the same number for equal choices. */
    @NotEmpty
    @ElementCollection
    @CollectionTable(name = "lab_offering_engineers", joinColumns = @JoinColumn(name = "offering_id"))
    @OrderColumn(name = "position")
    private List<@Valid InstructorPriority> labEngineers = new ArrayList<>();
}
