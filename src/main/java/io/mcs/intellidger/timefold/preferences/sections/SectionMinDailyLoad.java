package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionMinDailyLoadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** That the section has at least so many periods on each day listed. */
@Getter
public class SectionMinDailyLoad extends SectionPreference {

    private final Map<DayOfWeek, Integer> minPeriodsPerDay;

    public SectionMinDailyLoad(Section section, int weight, Map<DayOfWeek, Integer> minPeriodsPerDay) {
        super(section, weight);
        this.minPeriodsPerDay = minPeriodsPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionMinDailyLoad> fetch(SectionMinDailyLoadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionMinDailyLoad(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getMinPeriodsPerDay())))
                .toList();
    }

    /** How many periods these fall short of its minimums, on each day it lists. */
    public int missedBy(Set<Period> periods) {
        var counts = Period.countByDay(periods);
        return minPeriodsPerDay.entrySet().stream()
                .mapToInt(entry -> Math.max(0, entry.getValue() - counts.getOrDefault(entry.getKey(), 0)))
                .sum();
    }

    /** A section under its fewest periods on a day: soft, the weight for each period short. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionMinDailyLoad.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section under its daily minimum");
    }
}
