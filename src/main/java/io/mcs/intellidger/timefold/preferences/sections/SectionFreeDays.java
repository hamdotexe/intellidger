package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionFreeDaysRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** That the section has so many days with no sessions, whichever days they are. */
@Getter
public class SectionFreeDays extends SectionPreference {

    private final int howManyDays;

    public SectionFreeDays(Section section, int weight, int howManyDays) {
        super(section, weight);
        this.howManyDays = howManyDays;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionFreeDays> fetch(SectionFreeDaysRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionFreeDays(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getHowManyDays()))
                .toList();
    }

    /** How many free days these periods leave it short of, among the teaching days. */
    public int missedBy(Set<Period> periods, Set<DayOfWeek> teachingDays) {
        return Math.max(0, howManyDays - (teachingDays.size() - Period.daysWith(periods)));
    }

    /** A section with fewer free days than it wants: soft, the weight for each day short. */
    public static Constraint missed(ConstraintFactory factory) {
        return Section.periodsAttended(factory)
                .join(SectionFreeDays.class,
                        Joiners.equal((section, periods) -> section, preference -> preference.getSection()))
                .join(Period.teachingDays(factory))
                .filter((section, periods, preference, days) -> preference.missedBy(periods, days) > 0)
                .penalize(HardSoftScore.ONE_SOFT, (section, periods, preference, days) ->
                        preference.getWeight() * preference.missedBy(periods, days))
                .asConstraint("Section short of its free days");
    }
}
