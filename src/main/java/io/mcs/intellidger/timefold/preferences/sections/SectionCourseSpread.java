package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionCourseSpreadRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** That two sessions of one course fall at least so many days apart. */
@Getter
public class SectionCourseSpread extends SectionPreference {

    private final int minDaysBetween;

    public SectionCourseSpread(Section section, int weight, int minDaysBetween) {
        super(section, weight);
        this.minDaysBetween = minDaysBetween;
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionCourseSpread> fetch(SectionCourseSpreadRepository preferences, UUID termId,
            Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionCourseSpread(timetable.section(preference.getSection().getId()),
                        preference.getWeight(), preference.getMinDaysBetween()))
                .toList();
    }

    /** How many days two sessions fall short of the days it wants between them. */
    public int missedBy(Session first, Session second) {
        int between = Math.abs(first.getDay().getValue() - second.getDay().getValue()) - 1;
        return Math.max(0, minDaysBetween - between);
    }

    /** Two sessions of one of a section's courses too few days apart: soft, the weight for each day short. */
    public static Constraint missed(ConstraintFactory factory) {
        return factory.forEachUniquePair(Session.class,
                        Joiners.equal(session -> session.getOffering().getSection()),
                        Joiners.equal(session -> session.getOffering().getCourse()))
                .join(SectionCourseSpread.class,
                        Joiners.equal((first, second) -> first.getOffering().getSection(),
                                preference -> preference.getSection()))
                .filter((first, second, preference) -> preference.missedBy(first, second) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        (first, second, preference) -> preference.getWeight() * preference.missedBy(first, second))
                .asConstraint("Section's course sessions too close");
    }
}
