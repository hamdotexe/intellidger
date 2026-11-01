package io.mcs.intellidger.entities.teachers;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/** A period a teacher can't teach: no session of theirs may use it. */
@Entity
@Audited
@DiscriminatorValue("HARD")
@Table(name = "teacher_hard_blocks")
public class TeacherHardBlock extends TeacherBlock {
}
