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
import io.mcs.intellidger.timefold.offerings.Offering;
import io.mcs.intellidger.timefold.teachers.Teacher;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/** Who teaches an offering, every session of it: one of its listed instructors, chosen by the solver. */
@PlanningEntity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstructorAssignment {

    @PlanningId
    private UUID id;

    private Offering offering;

    /** Its offering's listed instructors: nobody else is ever given it. */
    @ValueRangeProvider(id = "possibleInstructors")
    private List<Teacher> possibleInstructors;

    @Setter
    @PlanningVariable(valueRangeProviderRefs = "possibleInstructors")
    private Teacher teacher;

    public InstructorAssignment(UUID id, Offering offering) {
        this.id = id;
        this.offering = offering;
        this.possibleInstructors = offering.getInstructors().stream()
                .map(instructor -> instructor.getTeacher())
                .distinct()
                .toList();
    }

    /** Each offering's instructor: the draft's while still listed, otherwise none yet. */
    public static List<InstructorAssignment> fetch(TimetableDraftRepository drafts, UUID termId,
            Timetable timetable) {
        return assignedAs(drafts.findByTermId(termId).map(draft -> draft.getInstructorAssignments())
                .orElse(List.of()), timetable);
    }

    /** Each offering's instructor as a saved version has it, as for the draft. */
    public static List<InstructorAssignment> fetch(TimetableVersionRepository versions, UUID versionId,
            Timetable timetable) {
        return assignedAs(versions.findById(versionId).orElseThrow().getInstructorAssignments(), timetable);
    }

    /** Each offering's instructor: the saved one's while still listed, otherwise none yet. */
    private static List<InstructorAssignment> assignedAs(
            List<io.mcs.intellidger.entities.timetables.InstructorAssignment> saved, Timetable timetable) {
        return timetable.getOfferings().stream()
                .map(offering -> {
                    var found = saved.stream()
                            .filter(assigned -> assigned.getOffering().getId().equals(offering.getId()))
                            .findFirst();
                    var assignment = new InstructorAssignment(
                            found.map(assigned -> assigned.getId()).orElseGet(() -> UUID.randomUUID()), offering);
                    found.map(assigned -> timetable.teacher(assigned.getTeacher().getId()))
                            .filter(teacher -> assignment.getPossibleInstructors().contains(teacher))
                            .ifPresent(teacher -> assignment.setTeacher(teacher));
                    return assignment;
                })
                .toList();
    }

    /** An instructor below the offering's first priority: soft, the offering's instructor weight for each step. */
    public static Constraint belowFirst(ConstraintFactory factory) {
        return factory.forEach(InstructorAssignment.class)
                .filter(assignment -> assignment.getOffering().instructorCost(assignment.getTeacher()) > 0)
                .penalize(HardSoftScore.ONE_SOFT,
                        assignment -> assignment.getOffering().instructorCost(assignment.getTeacher()))
                .asConstraint("Instructor below the first priority");
    }
}
