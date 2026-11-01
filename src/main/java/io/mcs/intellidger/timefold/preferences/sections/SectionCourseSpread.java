package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.repositories.preferences.sections.SectionCourseSpreadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That two sessions of one course fall at least so many days apart. */
@Getter
public class SectionCourseSpread extends SectionPreference {

    private final int minDaysBetween;

    public SectionCourseSpread(Section section, int weight, int minDaysBetween) {
        super(section, weight);
        this.minDaysBetween = minDaysBetween;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionCourseSpread> fetch(SectionCourseSpreadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionCourseSpread(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getMinDaysBetween()))
                .toList();
    }
}
