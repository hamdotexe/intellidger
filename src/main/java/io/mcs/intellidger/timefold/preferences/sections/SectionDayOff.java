package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionDayOffRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

/** That the section has a given day with no sessions. */
@Getter
public class SectionDayOff extends SectionPreference {

    private final DayOfWeek day;

    public SectionDayOff(Section section, int weight, DayOfWeek day) {
        super(section, weight);
        this.day = day;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionDayOff> fetch(SectionDayOffRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionDayOff(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getDay()))
                .toList();
    }
}
