package io.mcs.intellidger.entities.sections;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/** A period a section can't have a session, as for a seminar: none of its sessions may use it. */
@Entity
@Audited
@DiscriminatorValue("HARD")
@Table(name = "section_hard_blocks")
public class SectionHardBlock extends SectionBlock {
}
