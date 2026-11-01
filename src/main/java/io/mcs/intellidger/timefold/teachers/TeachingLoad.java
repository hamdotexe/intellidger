package io.mcs.intellidger.timefold.teachers;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.teachers.TeachingLoadRepository;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** The most a teacher may teach, in periods, a day and a week; no timetable passes either. */
@Getter
@AllArgsConstructor
public class TeachingLoad {

    private final Teacher teacher;

    private final int maxPeriodsPerDay;

    private final int maxPeriodsPerWeek;

    /** The term's teaching loads, as saved. */
    public static List<TeachingLoad> fetch(TeachingLoadRepository loads, UUID termId, Timetable timetable) {
        return loads.findByTermId(termId).stream()
                .map(load -> new TeachingLoad(timetable.teacher(load.getTeacher().getId()), load.getMaxPeriodsPerDay(),
                        load.getMaxPeriodsPerWeek()))
                .toList();
    }

    /** A teacher teaching more periods in a day than their load allows: hard, for each period over. */
    public static Constraint overPerDay(ConstraintFactory factory) {
        return Teacher.sessionsTaught(factory)
                .groupBy((teacher, session) -> teacher, (teacher, session) -> session.getDay(),
                        ConstraintCollectors.sum((teacher, session) -> session.getLengthInPeriods()))
                .join(TeachingLoad.class, Joiners.equal((teacher, day, periods) -> teacher, load -> load.getTeacher()))
                .filter((teacher, day, periods, load) -> periods > load.getMaxPeriodsPerDay())
                .penalize(HardSoftScore.ONE_HARD, (teacher, day, periods, load) -> periods - load.getMaxPeriodsPerDay())
                .asConstraint("Teacher over their teaching load for a day");
    }

    /** A teacher teaching more periods in the week than their load allows: hard, for each period over. */
    public static Constraint overPerWeek(ConstraintFactory factory) {
        return Teacher.sessionsTaught(factory)
                .groupBy((teacher, session) -> teacher,
                        ConstraintCollectors.sum((teacher, session) -> session.getLengthInPeriods()))
                .join(TeachingLoad.class, Joiners.equal((teacher, periods) -> teacher, load -> load.getTeacher()))
                .filter((teacher, periods, load) -> periods > load.getMaxPeriodsPerWeek())
                .penalize(HardSoftScore.ONE_HARD, (teacher, periods, load) -> periods - load.getMaxPeriodsPerWeek())
                .asConstraint("Teacher over their teaching load for the week");
    }
}
