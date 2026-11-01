package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionOffByRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** That the section is done by a time on each day listed. */
@Getter
public class SectionOffBy extends SectionPreference {

    private final Map<DayOfWeek, LocalTime> offByPerDay;

    public SectionOffBy(Section section, int weight, Map<DayOfWeek, LocalTime> offByPerDay) {
        super(section, weight);
        this.offByPerDay = offByPerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionOffBy> fetch(SectionOffByRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionOffBy(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getOffByPerDay())))
                .toList();
    }

    /** How many of these periods end after its time on their day. */
    public int missedBy(Set<Period> periods) {
        return (int) periods.stream()
                .filter(period -> {
                    LocalTime by = offByPerDay.get(period.getDay());
                    return by != null && period.getEndsAt().isAfter(by);
                })
                .count();
    }

    /** A section taught after its time on a day: soft, the weight for each period that ends after it. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionOffBy.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section after its finish time");
    }
}
