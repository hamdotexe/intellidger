package io.mcs.intellidger.timefold;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import io.mcs.intellidger.entities.timetables.InstructorAssignment;
import io.mcs.intellidger.entities.timetables.LabEngineerAssignment;
import io.mcs.intellidger.entities.timetables.Session;
import io.mcs.intellidger.entities.timetables.TimetableDraft;
import io.mcs.intellidger.repositories.offerings.LabOfferingRepository;
import io.mcs.intellidger.repositories.offerings.TheoryOfferingRepository;
import io.mcs.intellidger.repositories.rooms.RoomRepository;
import io.mcs.intellidger.repositories.teachers.TeacherRepository;
import io.mcs.intellidger.repositories.terms.TermRepository;
import io.mcs.intellidger.repositories.timetables.TimetableDraftRepository;
import io.mcs.intellidger.repositories.weekplan.PeriodRepository;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Solves a term's draft and saves it: loads the problem, lets Timefold place every session and choose every teacher,
 * and keeps the result only when it breaks no hard rule. A run that breaks one has failed: nothing is saved, and the
 * draft stays as it was. The database is touched only at the two ends, each in a short transaction of its own, never
 * while solving.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SolveProblem {

    private final LoadProblem loadProblem;
    private final SolverManager<Timetable> solverManager;
    private final SolutionManager<Timetable, HardSoftScore> solutionManager;
    private final TransactionTemplate transactions;
    private final TimetableDraftRepository drafts;
    private final TermRepository terms;
    private final PeriodRepository periods;
    private final RoomRepository rooms;
    private final TeacherRepository teachers;
    private final TheoryOfferingRepository theoryOfferings;
    private final LabOfferingRepository labOfferings;

    /**
     * Starts solving the term's draft in the background and returns at once, its transaction covering the load alone.
     * While it runs, {@code onBetter} hears each better score. At the end it saves the timetable and tells
     * {@code onSaved} its score, or saves nothing and tells {@code onFailed} why. All three are called on the solver's
     * thread.
     */
    @Transactional(readOnly = true)
    public void start(UUID termId, Consumer<HardSoftScore> onBetter, Consumer<HardSoftScore> onSaved,
            Consumer<List<String>> onFailed) {
        Timetable problem = loadProblem.load(termId);
        solverManager.solveBuilder()
                .withProblemId(termId)
                .withProblem(problem)
                .withBestSolutionEventConsumer(event -> onBetter.accept(event.solution().getScore()))
                .withFinalBestSolutionEventConsumer(event -> finish(termId, event.solution(), onSaved, onFailed))
                .withExceptionHandler((_, error) -> {
                    log.error("Solving term {} stopped with an error", termId, error);
                    onFailed.accept(List.of("The run stopped with an error"));
                })
                .run();
    }

    /** Stops the term's run now: it ends as if its time were up, and is judged the same way. */
    public void stop(UUID termId) {
        solverManager.terminateEarly(termId);
    }

    /**
     * The term's draft as it stands, scored without moving anything: what it breaks and costs in all, and through
     * {@link Timetable#isComplete()} whether every session is placed, since one that isn't counts for nothing.
     */
    @Transactional(readOnly = true)
    public Timetable checkDraft(UUID termId) {
        Timetable draft = loadProblem.load(termId);
        solutionManager.update(draft);
        return draft;
    }

    /** A saved version against its term as the term stands now, scored without moving anything, as for the draft. */
    @Transactional(readOnly = true)
    public Timetable checkVersion(UUID versionId) {
        Timetable version = loadProblem.loadVersion(versionId);
        solutionManager.update(version);
        return version;
    }

    /** Judges how a run ended: saves a complete timetable that breaks no hard rule, and says why not otherwise. */
    private void finish(UUID termId, Timetable solved, Consumer<HardSoftScore> onSaved,
            Consumer<List<String>> onFailed) {
        if (!solved.isComplete()) {
            onFailed.accept(List.of("Stopped before every session was placed"));
            return;
        }
        HardSoftScore score = solved.getScore();
        if (!score.isFeasible()) {
            onFailed.accept(List.of("Breaks hard rules, scoring " + score.hardScore() + " on them"));
            return;
        }
        transactions.executeWithoutResult(_ -> save(termId, solved));
        onSaved.accept(score);
    }

    /** Writes the solved timetable into the term's draft, making the draft on its first run. */
    private void save(UUID termId, Timetable solved) {
        TimetableDraft draft = drafts.findByTermId(termId).orElseGet(() -> {
            var created = new TimetableDraft();
            created.setTerm(terms.getReferenceById(termId));
            return created;
        });
        saveSessions(draft, solved);
        saveInstructors(draft, solved);
        saveLabEngineers(draft, solved);
        drafts.save(draft);
    }

    /**
     * The draft's sessions made the solved ones: each it had moved to its new period and room, locks kept; each new one
     * added; each it had that the run no longer has, its offering or session lengths changed since, removed.
     */
    private void saveSessions(TimetableDraft draft, Timetable solved) {
        var solvedById = solved.getSessions().stream()
                .collect(Collectors.toMap(placed -> placed.getId(), placed -> placed));
        draft.getSessions().removeIf(saved -> !solvedById.containsKey(saved.getId()));
        var savedById = draft.getSessions().stream()
                .collect(Collectors.toMap(saved -> saved.getId(), saved -> saved));
        for (var placed : solved.getSessions()) {
            Session session = savedById.get(placed.getId());
            if (session == null) {
                session = new Session();
                session.setTimetable(draft);
                session.setOffering(placed.getOffering() instanceof LabOffering
                        ? labOfferings.getReferenceById(placed.getOffering().getId())
                        : theoryOfferings.getReferenceById(placed.getOffering().getId()));
                session.setLengthInPeriods(placed.getLengthInPeriods());
                session.setLocked(placed.isLocked());
                draft.getSessions().add(session);
            }
            session.setStartPeriod(periods.getReferenceById(placed.getStartPeriod().getId()));
            session.setRoom(rooms.getReferenceById(placed.getRoom().getId()));
        }
    }

    /** The draft's instructors made the solved ones, as for sessions. */
    private void saveInstructors(TimetableDraft draft, Timetable solved) {
        var solvedById = solved.getInstructorAssignments().stream()
                .collect(Collectors.toMap(assigned -> assigned.getId(), assigned -> assigned));
        draft.getInstructorAssignments().removeIf(saved -> !solvedById.containsKey(saved.getId()));
        var savedById = draft.getInstructorAssignments().stream()
                .collect(Collectors.toMap(saved -> saved.getId(), saved -> saved));
        for (var assigned : solved.getInstructorAssignments()) {
            InstructorAssignment assignment = savedById.get(assigned.getId());
            if (assignment == null) {
                assignment = new InstructorAssignment();
                assignment.setTimetable(draft);
                assignment.setOffering(assigned.getOffering() instanceof LabOffering
                        ? labOfferings.getReferenceById(assigned.getOffering().getId())
                        : theoryOfferings.getReferenceById(assigned.getOffering().getId()));
                draft.getInstructorAssignments().add(assignment);
            }
            assignment.setTeacher(teachers.getReferenceById(assigned.getTeacher().getId()));
        }
    }

    /** The draft's lab engineers made the solved ones, as for sessions. */
    private void saveLabEngineers(TimetableDraft draft, Timetable solved) {
        var solvedById = solved.getLabEngineerAssignments().stream()
                .collect(Collectors.toMap(assigned -> assigned.getId(), assigned -> assigned));
        draft.getLabEngineerAssignments().removeIf(saved -> !solvedById.containsKey(saved.getId()));
        var savedById = draft.getLabEngineerAssignments().stream()
                .collect(Collectors.toMap(saved -> saved.getId(), saved -> saved));
        for (var assigned : solved.getLabEngineerAssignments()) {
            LabEngineerAssignment assignment = savedById.get(assigned.getId());
            if (assignment == null) {
                assignment = new LabEngineerAssignment();
                assignment.setTimetable(draft);
                assignment.setOffering(labOfferings.getReferenceById(assigned.getOffering().getId()));
                draft.getLabEngineerAssignments().add(assignment);
            }
            assignment.setTeacher(teachers.getReferenceById(assigned.getTeacher().getId()));
        }
    }
}
