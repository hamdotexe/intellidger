package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionMinDailyLoadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the section has at least so many periods on each day listed. */
@Getter
public class SectionMinDailyLoad extends SectionPreference {

    private final Map<DayOfWeek, Integer> minPeriodsPerDay;

    public SectionMinDailyLoad(Section section, int weight, Map<DayOfWeek, Integer> minPeriodsPerDay) {
        super(section, weight);
        this.minPeriodsPerDay = minPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionMinDailyLoad> fetch(SectionMinDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionMinDailyLoad(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMinPeriodsPerDay())))
                .toList();
    }
}
