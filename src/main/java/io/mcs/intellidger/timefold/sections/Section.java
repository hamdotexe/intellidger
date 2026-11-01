package io.mcs.intellidger.timefold.sections;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import ai.timefold.solver.core.api.score.stream.bi.BiConstraintStream;
import io.mcs.intellidger.timefold.batches.Batch;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

/** Students taught together: a batch's regular section, or an elective section drawn from the whole batch. */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Section {

    private final UUID id;

    /** As timetables print it, such as "BESE-30-C" or "IoT-1". */
    private final String name;

    /** The batch it is part of: a regular section's own, or the one an elective section draws its students from. */
    private final Batch batch;

    /**
     * Whether the other section's sessions keep this one's students busy too: the same section, or a regular section
     * and an elective section of one batch.
     */
    public boolean sharesStudentsWith(Section other) {
        return this == other || (batch.equals(other.getBatch())
                && (this instanceof ElectiveSection) != (other instanceof ElectiveSection));
    }

    /**
     * Each session a section's students are at: its own, and for a regular section every session of its batch's
     * elective sections, which draw their students from it.
     */
    public static BiConstraintStream<Section, Session> sessionsAttended(ConstraintFactory factory) {
        return factory.forEach(Session.class)
                .map(session -> session.getOffering().getSection(), session -> session)
                .concat(factory.forEach(RegularSection.class)
                        .join(Session.class,
                                Joiners.equal(section -> section.getBatch(),
                                        session -> session.getOffering().getSection().getBatch()),
                                Joiners.filtering((section, session) ->
                                        session.getOffering().getSection() instanceof ElectiveSection))
                        .map((section, session) -> section, (section, session) -> session));
    }

    /** The periods each section's students are at a session in: a period two electives share counts once. */
    public static BiConstraintStream<Section, Set<Period>> periodsAttended(ConstraintFactory factory) {
        return sessionsAttended(factory)
                .join(Period.class,
                        Joiners.equal((section, session) -> session.getDay(), period -> period.getDay()),
                        Joiners.filtering((section, session, period) -> session.covers(period)))
                .groupBy((section, session, period) -> section,
                        ConstraintCollectors.toSet((section, session, period) -> period));
    }

    /**
     * A section at two sessions in one period, an elective section's counting for every regular section of its batch:
     * hard, for each period they share. Elective sections meeting at once are the term's rule.
     */
    public static Constraint doubleBooked(ConstraintFactory factory) {
        return factory.forEachUniquePair(Session.class,
                        Joiners.equal(session -> session.getOffering().getSection().getBatch()),
                        Joiners.equal(session -> session.getDay()),
                        Joiners.overlapping(session -> session.getFirstPeriodNumber(),
                                session -> session.getEndPeriodNumber()))
                .filter((first, second) ->
                        first.getOffering().getSection().sharesStudentsWith(second.getOffering().getSection()))
                .penalize(HardSoftScore.ONE_HARD, (first, second) -> first.periodsSharedWith(second))
                .asConstraint("Section at two sessions at once");
    }

    /** A section with one course twice in a day, theory and lab included: hard, for each two sessions of it. */
    public static Constraint courseTwiceADay(ConstraintFactory factory) {
        return factory.forEachUniquePair(Session.class,
                        Joiners.equal(session -> session.getOffering().getSection()),
                        Joiners.equal(session -> session.getOffering().getCourse()),
                        Joiners.equal(session -> session.getDay()))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Section with a course twice in a day");
    }
}
