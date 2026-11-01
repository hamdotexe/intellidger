package io.mcs.intellidger.timefold.weekplan;

import io.mcs.intellidger.repositories.weekplan.PeriodRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/** One period of the term's week, such as the 1st on Monday from 08:00 to 08:50: where sessions start and run. */
@Getter
@AllArgsConstructor
public class Period {

    private final UUID id;

    private final DayOfWeek day;

    /** Its place in the day, from 1. Two periods with numbers next to each other are consecutive. */
    private final int number;

    private final LocalTime startsAt;

    private final LocalTime endsAt;

    /** The term's periods, as saved. */
    public static List<Period> fetch(PeriodRepository periods, UUID termId) {
        return periods.findByTermId(termId).stream()
                .map(period -> new Period(period.getId(), period.getDay(), period.getNumber(), period.getStartsAt(),
                        period.getEndsAt()))
                .toList();
    }
}
