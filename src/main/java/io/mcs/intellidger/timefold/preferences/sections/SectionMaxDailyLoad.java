package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionMaxDailyLoadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the section has at most so many periods on each day listed. */
@Getter
public class SectionMaxDailyLoad extends SectionPreference {

    private final Map<DayOfWeek, Integer> maxPeriodsPerDay;

    public SectionMaxDailyLoad(Section section, int weight, Map<DayOfWeek, Integer> maxPeriodsPerDay) {
        super(section, weight);
        this.maxPeriodsPerDay = maxPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionMaxDailyLoad> fetch(SectionMaxDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionMaxDailyLoad(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMaxPeriodsPerDay())))
                .toList();
    }
}
