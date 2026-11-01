package io.mcs.intellidger.entities.preferences.sections;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.envers.Audited;

/**
 * That the section stays in one room from a session to the one right after it: a timetable pays the weight for each
 * change of room between back-to-back sessions.
 */
@Entity
@Audited
@DiscriminatorValue("SAME_ROOM")
@Table(name = "section_same_rooms")
public class SectionSameRoom extends SectionPreference {
}
