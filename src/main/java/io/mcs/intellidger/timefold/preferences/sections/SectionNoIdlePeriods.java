package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionNoIdlePeriodsRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the section's days have no free periods between their first session and their last. */
public class SectionNoIdlePeriods extends SectionPreference {

    public SectionNoIdlePeriods(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionNoIdlePeriods> fetch(SectionNoIdlePeriodsRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionNoIdlePeriods(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }
}
