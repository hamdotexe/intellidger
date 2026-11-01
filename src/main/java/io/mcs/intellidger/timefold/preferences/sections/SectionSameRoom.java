package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionSameRoomRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the section stays in one room from a session to the one right after it. */
public class SectionSameRoom extends SectionPreference {

    public SectionSameRoom(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionSameRoom> fetch(SectionSameRoomRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionSameRoom(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }
}
