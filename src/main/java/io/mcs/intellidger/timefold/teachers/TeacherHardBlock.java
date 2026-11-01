package io.mcs.intellidger.timefold.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.teachers.TeacherHardBlockRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;

import java.util.List;
import java.util.UUID;

/** A period a teacher is never used in. */
public class TeacherHardBlock extends TeacherBlock {

    public TeacherHardBlock(Period period, Teacher teacher) {
        super(period, teacher);
    }

    /** The term's blocks of this kind, as saved. */
    public static List<TeacherHardBlock> fetch(TeacherHardBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new TeacherHardBlock(timetable.period(block.getPeriod().getId()),
                        timetable.teacher(block.getTeacher().getId())))
                .toList();
    }

    /** A teacher at a session in their hard block: hard. */
    public static Constraint used(ConstraintFactory factory) {
        return Teacher.sessionsAttended(factory)
                .join(TeacherHardBlock.class,
                        Joiners.equal((teacher, session) -> teacher, block -> block.getTeacher()),
                        Joiners.filtering((teacher, session, block) -> session.covers(block.getPeriod())))
                .penalize(HardSoftScore.ONE_HARD)
                .asConstraint("Teacher at a session in their hard block");
    }
}
