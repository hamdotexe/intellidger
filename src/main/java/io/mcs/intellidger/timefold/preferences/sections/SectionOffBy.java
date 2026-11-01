package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionOffByRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the section is done by a time on each day listed. */
@Getter
public class SectionOffBy extends SectionPreference {

    private final Map<DayOfWeek, LocalTime> offByPerDay;

    public SectionOffBy(Section section, int weight, Map<DayOfWeek, LocalTime> offByPerDay) {
        super(section, weight);
        this.offByPerDay = offByPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionOffBy> fetch(SectionOffByRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionOffBy(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getOffByPerDay())))
                .toList();
    }
}
