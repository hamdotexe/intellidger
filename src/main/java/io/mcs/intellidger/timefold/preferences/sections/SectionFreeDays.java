package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionFreeDaysRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That the section has so many days with no sessions, whichever days they are. */
@Getter
public class SectionFreeDays extends SectionPreference {

    private final int howManyDays;

    public SectionFreeDays(Section section, int weight, int howManyDays) {
        super(section, weight);
        this.howManyDays = howManyDays;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionFreeDays> fetch(SectionFreeDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionFreeDays(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getHowManyDays()))
                .toList();
    }
}
