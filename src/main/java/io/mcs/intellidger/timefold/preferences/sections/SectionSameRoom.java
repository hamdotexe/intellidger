package io.mcs.intellidger.timefold.preferences.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.preferences.sections.SectionSameRoomRepository;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.UUID;

/** That the section stays in one room from a session to the one right after it. */
public class SectionSameRoom extends SectionPreference {

    public SectionSameRoom(Section section, int weight) {
        super(section, weight);
    }

    /** The term's wishes of this kind, as saved. */
    public static List<SectionSameRoom> fetch(SectionSameRoomRepository preferences, UUID termId, Timetable timetable) {
        return preferences.findByTermId(termId).stream()
                .map(preference -> new SectionSameRoom(timetable.section(preference.getSection().getId()),
                        preference.getWeight()))
                .toList();
    }

    /** A section changing rooms between back-to-back sessions of its own: soft, the weight for each change. */
    public static Constraint missed(ConstraintFactory factory) {
        return factory.forEach(Session.class)
                .join(Session.class,
                        Joiners.equal(session -> session.getOffering().getSection(),
                                next -> next.getOffering().getSection()),
                        Joiners.equal(session -> session.getDay(), next -> next.getDay()),
                        Joiners.equal(session -> session.getEndPeriodNumber(), next -> next.getFirstPeriodNumber()))
                .filter((session, next) -> !session.getRoom().equals(next.getRoom()))
                .join(SectionSameRoom.class,
                        Joiners.equal((session, next) -> session.getOffering().getSection(),
                                preference -> preference.getSection()))
                .penalize(HardSoftScore.ONE_SOFT, (session, next, preference) -> preference.getWeight())
                .asConstraint("Section changing rooms back to back");
    }
}
