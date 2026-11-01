package io.mcs.intellidger.entities.offerings;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/** A course's theory for a section in a term: lectures, each taught by its one instructor. */
@Entity
@Audited
@DiscriminatorValue("THEORY")
@Table(name = "theory_offerings")
public class TheoryOffering extends Offering {
}
