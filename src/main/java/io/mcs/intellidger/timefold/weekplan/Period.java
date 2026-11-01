package io.mcs.intellidger.timefold.weekplan;

import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.uni.UniConstraintStream;
import io.mcs.intellidger.repositories.weekplan.PeriodRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

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

    /** The days the week has periods on: every day a section or teacher can be taught. */
    public static UniConstraintStream<Set<DayOfWeek>> teachingDays(ConstraintFactory factory) {
        return factory.forEach(Period.class)
                .groupBy(ConstraintCollectors.toSet(period -> period.getDay()));
    }

    /** How many of these periods fall on the day. */
    public static int countOn(Collection<Period> periods, DayOfWeek day) {
        return (int) periods.stream().filter(period -> period.getDay() == day).count();
    }

    /** How many of these periods fall on each day, a day with none left out. */
    public static Map<DayOfWeek, Integer> countByDay(Collection<Period> periods) {
        return periods.stream()
                .collect(Collectors.groupingBy(period -> period.getDay(), Collectors.summingInt(period -> 1)));
    }

    /** How many days these periods fall on. */
    public static int daysWith(Collection<Period> periods) {
        return (int) periods.stream().map(period -> period.getDay()).distinct().count();
    }

    /** How many free periods lie between the first and the last of these on each day, every day added up. */
    public static int idleBetween(Collection<Period> periods) {
        return byDay(periods).values().stream()
                .mapToInt(numbers -> numbers.last() - numbers.first() + 1 - numbers.size())
                .sum();
    }

    /**
     * How many of these periods come after the most in a row: a run goes on through consecutive periods, whatever
     * break lies between them, and only a free period ends it.
     */
    public static int beyondInARow(Collection<Period> periods, int maxPeriods) {
        int beyond = 0;
        for (TreeSet<Integer> numbers : byDay(periods).values()) {
            int run = 0;
            int previous = 0;
            for (int number : numbers) {
                run = number == previous + 1 ? run + 1 : 1;
                previous = number;
                if (run > maxPeriods) {
                    beyond++;
                }
            }
        }
        return beyond;
    }

    /**
     * How many periods these loads, one a day, are away from an even share of their total over the teaching days. A
     * day may take the share rounded down or up, and pays for each period below the one or above the other.
     */
    public static int awayFromEven(Map<DayOfWeek, Integer> loads, Set<DayOfWeek> teachingDays) {
        if (teachingDays.isEmpty()) {
            return 0;
        }
        int total = teachingDays.stream().mapToInt(day -> loads.getOrDefault(day, 0)).sum();
        int fewest = total / teachingDays.size();
        int most = total % teachingDays.size() == 0 ? fewest : fewest + 1;
        return teachingDays.stream()
                .mapToInt(day -> {
                    int load = loads.getOrDefault(day, 0);
                    return Math.max(0, fewest - load) + Math.max(0, load - most);
                })
                .sum();
    }

    /** The numbers of these periods, day by day, in order. */
    private static Map<DayOfWeek, TreeSet<Integer>> byDay(Collection<Period> periods) {
        return periods.stream()
                .collect(Collectors.groupingBy(period -> period.getDay(), Collectors.mapping(
                        period -> period.getNumber(), Collectors.toCollection(() -> new TreeSet<Integer>()))));
    }
}
