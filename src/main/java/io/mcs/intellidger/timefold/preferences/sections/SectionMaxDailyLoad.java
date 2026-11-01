package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionMaxDailyLoadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** That the section has at most so many periods on each day listed. */
@Getter
public class SectionMaxDailyLoad extends SectionPreference {

    private final Map<DayOfWeek, Integer> maxPeriodsPerDay;

    public SectionMaxDailyLoad(Section section, int weight, Map<DayOfWeek, Integer> maxPeriodsPerDay) {
        super(section, weight);
        this.maxPeriodsPerDay = maxPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionMaxDailyLoad> fetch(SectionMaxDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionMaxDailyLoad(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMaxPeriodsPerDay())))
                .toList();
    }

    /** How many periods these go over its maximums, on each day it lists. */
    public int missedBy(Set<Period> periods) {
        var counts = Period.countByDay(periods);
        return maxPeriodsPerDay.entrySet().stream()
                .mapToInt(entry -> Math.max(0, counts.getOrDefault(entry.getKey(), 0) - entry.getValue()))
                .sum();
    }

    /** A section over its most periods on a day: soft, the weight for each period over. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionMaxDailyLoad.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section over its daily maximum");
    }
}
