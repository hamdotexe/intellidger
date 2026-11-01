package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionDayOffRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the section has a given day with no sessions. */
@Getter
public class SectionDayOff extends SectionPreference {

    private final DayOfWeek day;

    public SectionDayOff(Section section, int weight, DayOfWeek day) {
        super(section, weight);
        this.day = day;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionDayOff> fetch(SectionDayOffRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionDayOff(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getDay()))
                .toList();
    }

    /** How many of these periods fall on its day. */
    public int missedBy(Set<Period> periods) {
        return Period.countOn(periods, day);
    }

    /** A section with sessions on its day off: soft, the weight for each period that day. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionDayOff.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section on its day off");
    }
}
