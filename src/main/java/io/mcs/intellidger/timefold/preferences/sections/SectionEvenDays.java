package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionEvenDaysRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the section's load is spread evenly over every teaching day of the week. */
public class SectionEvenDays extends SectionPreference {

    public SectionEvenDays(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionEvenDays> fetch(SectionEvenDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionEvenDays(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }
}
