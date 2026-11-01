package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionNoIdlePeriodsRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the section's days have no free periods between their first session and their last. */
public class SectionNoIdlePeriods extends SectionPreference {

    public SectionNoIdlePeriods(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionNoIdlePeriods> fetch(SectionNoIdlePeriodsRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionNoIdlePeriods(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }

    /** How many free periods lie between these, on each day between its first and its last. */
    public int missedBy(Set<Period> periods) {
        return Period.idleBetween(periods);
    }

    /** A section with free periods inside its days: soft, the weight for each one. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionNoIdlePeriods.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section idle between sessions");
    }
}
