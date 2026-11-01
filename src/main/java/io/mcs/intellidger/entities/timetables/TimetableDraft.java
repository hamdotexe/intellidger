package io.mcs.intellidger.entities.timetables;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/**
 * The timetable being worked on in a term: generated, optimized and changed by hand. Saving freezes a copy of it as a
 * version; the draft goes on.
 */
@Entity
@Audited
@DiscriminatorValue("DRAFT")
@Table(name = "timetable_drafts")
public class TimetableDraft extends Timetable {
}
