package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionNotBeforeRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** That the section starts no earlier than a time on each day listed. */
@Getter
public class SectionNotBefore extends SectionPreference {

    private final Map<DayOfWeek, LocalTime> notBeforePerDay;

    public SectionNotBefore(Section section, int weight, Map<DayOfWeek, LocalTime> notBeforePerDay) {
        super(section, weight);
        this.notBeforePerDay = notBeforePerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionNotBefore> fetch(SectionNotBeforeRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionNotBefore(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getNotBeforePerDay())))
                .toList();
    }
}
