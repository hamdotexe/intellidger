package io.mcs.intellidger.timefold.preferences.sections;

import io.mcs.intellidger.timefold.sections.Section;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** Something a section would like from its week, and how much it matters. Each kind of wish is its own class. */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SectionPreference {

    private final Section section;

    /** What a timetable pays for each period or day it misses the wish by. */
    private final int weight;
}
