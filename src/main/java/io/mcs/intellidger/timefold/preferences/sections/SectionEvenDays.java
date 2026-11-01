package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionEvenDaysRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the section's load is spread evenly over every teaching day of the week. */
public class SectionEvenDays extends SectionPreference {

    public SectionEvenDays(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionEvenDays> fetch(SectionEvenDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionEvenDays(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }

    /** How many periods these are away from an even share of each teaching day. */
    public int missedBy(Set<Period> periods, Set<DayOfWeek> teachingDays) {
        return Period.awayFromEven(Period.countByDay(periods), teachingDays);
    }

    /** A section's load spread unevenly: soft, the weight for each period a day is away from an even share. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionEvenDays.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .join(Period.teachingDays(factory))
                .filter((section, periods, preference, days) -> preference.missedBy(periods, days) > 0)
                .penalize(HardSoftScore.ONE_SOFT, (section, periods, preference, days) ->
                        preference.getWeight() * preference.missedBy(periods, days))
                .asConstraint("Section's load uneven over the week");
    }
}
