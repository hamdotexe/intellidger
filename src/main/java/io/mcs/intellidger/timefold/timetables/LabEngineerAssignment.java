package io.mcs.intellidger.timefold.timetables;

import ai.timefold.solver.core.api.domain.common.PlanningId;
import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import io.mcs.intellidger.repositories.timetables.TimetableDraftRepository;
import io.mcs.intellidger.repositories.timetables.TimetableVersionRepository;
import io.mcs.intellidger.timefold.offerings.LabOffering;
import io.mcs.intellidger.timefold.teachers.Teacher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/** Who is a lab offering's lab engineer, every session of it: one of its listed lab engineers, chosen by the solver. */
@PlanningEntity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LabEngineerAssignment {

    @PlanningId
    private UUID id;

    private LabOffering offering;

    /** Its offering's listed lab engineers: nobody else is ever given it. */
    @ValueRangeProvider(id = "possibleLabEngineers")
    private List<Teacher> possibleLabEngineers;

    @Setter
    @PlanningVariable(valueRangeProviderRefs = "possibleLabEngineers")
    private Teacher teacher;

    public LabEngineerAssignment(UUID id, LabOffering offering) {
        this.id = id;
        this.offering = offering;
        this.possibleLabEngineers = offering.getLabEngineers().stream()
                .map(engineer -> engineer.getTeacher())
                .distinct()
                .toList();
    }

    /** Each lab offering's lab engineer: the draft's while still listed, otherwise none yet. */
    public static List<LabEngineerAssignment> fetch(TimetableDraftRepository drafts, UUID termId,
            Timetable timetable) {
        return assignedAs(drafts.findByTermId(termId).map(draft -> draft.getLabEngineerAssignments())
                .orElse(List.of()), timetable);
    }

    /** Each lab offering's lab engineer as a saved version has it, as for the draft. */
    public static List<LabEngineerAssignment> fetch(TimetableVersionRepository versions, UUID versionId,
            Timetable timetable) {
        return assignedAs(versions.findById(versionId).orElseThrow().getLabEngineerAssignments(), timetable);
    }

    /** Each lab offering's lab engineer: the saved one's while still listed, otherwise none yet. */
    private static List<LabEngineerAssignment> assignedAs(
            List<io.mcs.intellidger.entities.timetables.LabEngineerAssignment> saved, Timetable timetable) {
        return timetable.getLabOfferings().stream()
                .map(offering -> {
                    var found = saved.stream()
                            .filter(assigned -> assigned.getOffering().getId().equals(offering.getId()))
                            .findFirst();
                    var assignment = new LabEngineerAssignment(
                            found.map(assigned -> assigned.getId()).orElseGet(() -> UUID.randomUUID()), offering);
                    found.map(assigned -> timetable.teacher(assigned.getTeacher().getId()))
                            .filter(teacher -> assignment.getPossibleLabEngineers().contains(teacher))
                            .ifPresent(teacher -> assignment.setTeacher(teacher));
                    return assignment;
                })
                .toList();
    }

    /** A lab engineer below the lab's first priority: soft, the lab's lab engineer weight for each step. */
    public static Constraint belowFirst(ConstraintFactory factory) {
        return factory.forEach(LabEngineerAssignment.class)
                .filter(assignment -> assignment.getOffering().labEngineerCost(assignment.getTeacher()) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        assignment -> assignment.getOffering().labEngineerCost(assignment.getTeacher()))
                .asConstraint("Lab engineer below the first priority");
    }
}
