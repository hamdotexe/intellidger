package io.mcs.intellidger.timefold.teachers;

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
}
