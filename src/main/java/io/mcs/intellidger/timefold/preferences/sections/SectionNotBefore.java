package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionNotBeforeRepository;
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

/** That the section starts no earlier than a time on each day listed. */
@Getter
public class SectionNotBefore extends SectionPreference {

    private final Map<DayOfWeek, LocalTime> notBeforePerDay;

    public SectionNotBefore(Section section, int weight, Map<DayOfWeek, LocalTime> notBeforePerDay) {
        super(section, weight);
        this.notBeforePerDay = notBeforePerDay;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionNotBefore> fetch(SectionNotBeforeRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionNotBefore(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), Map.copyOf(preference.getNotBeforePerDay())))
                .toList();
    }

    /** How many of these periods start before its time on their day. */
    public int missedBy(Set<Period> periods) {
        return (int) periods.stream()
                .filter(period -> {
                    LocalTime from = notBeforePerDay.get(period.getDay());
                    return from != null && period.getStartsAt().isBefore(from);
                })
                .count();
    }

    /** A section taught before its time on a day: soft, the weight for each period that starts before it. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionNotBefore.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .filter((section, periods, preference) -> preference.missedBy(periods) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (section, periods, preference) -> preference.getWeight() * preference.missedBy(periods))
                .asConstraint("Section before its start time");
    }
}
