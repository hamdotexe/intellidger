package io.mcs.intellidger.timefold;

import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.ConstraintProvider;
import io.mcs.intellidger.timefold.preferences.sections.*;
import io.mcs.intellidger.timefold.preferences.teachers.*;
import io.mcs.intellidger.timefold.rooms.*;
import io.mcs.intellidger.timefold.sections.*;
import io.mcs.intellidger.timefold.teachers.*;
import io.mcs.intellidger.timefold.terms.Term;
import io.mcs.intellidger.timefold.timetables.*;

/**
 * The rules a timetable is scored by, each kept by the class whose rows state it: every hard rule, and every soft rule
 * at its own weight. Timefold calls this once, when the solver is built, and scores every run with what it returns.
 */
public class TimetableConstraints implements ConstraintProvider {

    @Override
    public Constraint[] defineConstraints(ConstraintFactory factory) {
        return new Constraint[] {
                // Hard: a timetable breaking any of these has failed.
                Session.impossiblePlacement(factory),
                Room.overfilled(factory),
                RoomHardBlock.used(factory),
                Teacher.doubleBooked(factory),
                TeacherHardBlock.used(factory),
                TeachingLoad.overPerDay(factory),
                TeachingLoad.overPerWeek(factory),
                Section.doubleBooked(factory),
                Section.courseTwiceADay(factory),
                SectionHardBlock.used(factory),
                Term.electivesAtOnce(factory),

                // Soft: each at its own weight.
                RoomSoftBlock.used(factory),
                TeacherSoftBlock.used(factory),
                SectionSoftBlock.used(factory),
                InstructorAssignment.belowFirst(factory),
                LabEngineerAssignment.belowFirst(factory),
                Session.roomBelowFirst(factory),
                SectionCourseSpread.missed(factory),
                SectionDayOff.missed(factory),
                SectionEvenDays.missed(factory),
                SectionFreeDays.missed(factory),
                SectionMaxDailyLoad.missed(factory),
                SectionMinDailyLoad.missed(factory),
                SectionNoIdlePeriods.missed(factory),
                SectionNotBefore.missed(factory),
                SectionOffBy.missed(factory),
                SectionPeriodsInARow.missed(factory),
                SectionSameRoom.missed(factory),
                TeacherDayOff.missed(factory),
                TeacherEvenDays.missed(factory),
                TeacherFreeDays.missed(factory),
                TeacherMaxDailyLoad.missed(factory),
                TeacherMinDailyLoad.missed(factory),
                TeacherMaxWeeklyLoad.missed(factory),
                TeacherMinWeeklyLoad.missed(factory),
                TeacherNoIdlePeriods.missed(factory),
                TeacherNotBefore.missed(factory),
                TeacherOffBy.missed(factory),
                TeacherPeriodsInARow.missed(factory),
        };
    }
}
