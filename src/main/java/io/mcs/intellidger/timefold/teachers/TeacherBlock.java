package io.mcs.intellidger.timefold.teachers;

import io.mcs.intellidger.timefold.weekplan.Block;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

/** A period a teacher is blocked on, hard or soft. */
@Getter
public abstract class TeacherBlock extends Block {

    private final Teacher teacher;

    protected TeacherBlock(Period period, Teacher teacher) {
        super(period);
        this.teacher = teacher;
    }
}
