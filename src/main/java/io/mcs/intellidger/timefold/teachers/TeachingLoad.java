package io.mcs.intellidger.timefold.teachers;

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
}
