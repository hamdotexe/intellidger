package io.mcs.intellidger.timefold.teachers;

import io.mcs.intellidger.repositories.teachers.TeacherSoftBlockRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A period a teacher would rather not be used in: a session may use it, at the cost of its weight. */
@Getter
public class TeacherSoftBlock extends TeacherBlock {

    /** What a timetable pays when a session uses the period anyway. */
    private final int weight;

    public TeacherSoftBlock(Period period, Teacher teacher, int weight) {
        super(period, teacher);
        this.weight = weight;
    }

    /** The term's blocks of this kind, as saved. */
    public static List<TeacherSoftBlock> fetch(TeacherSoftBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new TeacherSoftBlock(timetable.period(block.getPeriod().getId()),
                        timetable.teacher(block.getTeacher().getId()), block.getWeight()))
                .toList();
    }
}
