package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionPeriodsInARowRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.util.List;
import java.util.Set;
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

    /** How many of these periods come after its most in a row. */
    public int missedBy(Set<Period> periods) {
        return Period.beyondInARow(periods, maxPeriods);
    }

    /** A section sitting more periods in a row than it wants: soft, the weight for each period beyond. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionPeriodsInARow.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section too many periods in a row");
    }
}
