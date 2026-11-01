package io.mcs.intellidger.timefold.terms;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.terms.TermRepository;
import io.mcs.intellidger.timefold.sections.ElectiveSection;
import io.mcs.intellidger.timefold.timetables.Session;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * The term's own rules, its term preferences, for a run: never weighed, the same for every session. Whether electives
 * are mutually exclusive is a rule of the term's own; whether instructors attend labs is read by a teacher's rule, as
 * part of whom a lab keeps busy.
 */
@Getter
@AllArgsConstructor
public class Term {

    /** Whether a lab's instructor must be free for its whole session, beside its lab engineer. */
    private final boolean instructorsAttendLabs;

    /** Whether each student takes just one of their batch's electives, so different electives may meet at once. */
    private final boolean electivesMutuallyExclusive;

    /** The term as saved. Called from a service, inside its transaction. */
    public static Term fetch(TermRepository terms, UUID termId) {
        var term = terms.findById(termId).orElseThrow();
        return new Term(term.getInstructorsAttendLabs(), term.getElectivesMutuallyExclusive());
    }

    /**
     * Sections of two different electives of one batch meeting at once, while the term says a student may take more
     * than one: hard, for each period they share. Sections of one elective, such as IoT-1 and IoT-2, may meet at once.
     */
    public static Constraint electivesAtOnce(ConstraintFactory factory) {
        return factory.forEach(Session.class)
                .filter(session -> session.getOffering().getSection() instanceof ElectiveSection)
                .join(factory.forEach(Session.class)
                                .filter(session -> session.getOffering().getSection() instanceof ElectiveSection),
                        Joiners.lessThan(session -> session.getId()),
                        Joiners.equal(session -> session.getOffering().getSection().getBatch()),
                        Joiners.equal(session -> session.getDay()),
                        Joiners.overlapping(session -> session.getFirstPeriodNumber(),
                                session -> session.getEndPeriodNumber()))
                .filter((first, second) -> !first.getOffering().getCourse().equals(second.getOffering().getCourse()))
                .ifExists(Term.class, Joiners.filtering((first, second, term) -> !term.isElectivesMutuallyExclusive()))
                .penalize(HardSoftScore.ONE_HARD, (first, second) -> first.periodsSharedWith(second))
                .asConstraint("Sections of different electives at the same time");
    }
}
