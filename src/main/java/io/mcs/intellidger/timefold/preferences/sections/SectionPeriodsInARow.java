package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionPeriodsInARowRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the section sits no more than so many periods in a row. */
@Getter
public class SectionPeriodsInARow extends SectionPreference {

    private final int maxPeriods;

    public SectionPeriodsInARow(Section section, int weight, int maxPeriods) {
        super(section, weight);
        this.maxPeriods = maxPeriods;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionPeriodsInARow> fetch(SectionPeriodsInARowRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionPeriodsInARow(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getMaxPeriods()))
                .toList();
    }
}
