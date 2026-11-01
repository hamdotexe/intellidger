package io.mcs.intellidger.timefold.timetables;

import ai.timefold.solver.core.api.domain.common.PlanningId;
import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import io.mcs.intellidger.repositories.timetables.TimetableDraftRepository;
import io.mcs.intellidger.timefold.offerings.Offering;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * One session of an offering, which the solver places: the period it starts on and its room. It takes the next
 * periods of that day for as long as it lasts, which never changes. What it may be given is worked out once, when it
 * is built, so the solver never even tries the rest.
 */
@PlanningEntity
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Session {

    @PlanningId
    private UUID id;

    private Offering offering;

    private int lengthInPeriods;

    /** Every period it could start on with the whole session fitting into that day's next periods. */
    @ValueRangeProvider(id = "possibleStartPeriods")
    private List<Period> possibleStartPeriods;

    /**
     * Every room it may use: its offering's listed rooms when "only these rooms" is on, any room otherwise, and never
     * an impossible one.
     */
    @ValueRangeProvider(id = "possibleRooms")
    private List<Room> possibleRooms;

    @Setter
    @PlanningVariable(valueRangeProviderRefs = "possibleStartPeriods")
    private Period startPeriod;

    @Setter
    @PlanningVariable(valueRangeProviderRefs = "possibleRooms")
    private Room room;

    /** Kept where it is, room and time: the solver never moves it. */
    @Setter
    @PlanningPin
    private boolean locked;

    /** One of the offering's sessions, lasting one of its session lengths, among the term's periods and rooms. */
    public Session(UUID id, Offering offering, int lengthInPeriods, Collection<Period> periods,
            Collection<Room> rooms) {
        this.id = id;
        this.offering = offering;
        this.lengthInPeriods = lengthInPeriods;
        this.possibleStartPeriods = periods.stream()
                .filter(start -> IntStream.range(1, lengthInPeriods).allMatch(later -> periods.stream()
                        .anyMatch(period -> period.getDay() == start.getDay()
                                && period.getNumber() == start.getNumber() + later)))
                .toList();
        Collection<Room> allowed = offering.isOnlyListedRooms()
                ? offering.getRooms().stream().map(listed -> listed.getRoom()).toList()
                : rooms;
        this.possibleRooms = allowed.stream()
                .filter(room -> !offering.getImpossibleRooms().contains(room))
                .distinct()
                .toList();
    }

    /**
     * The term's sessions: one for each session length of each offering, placed and locked as the draft has it, or not
     * placed yet when the draft has none of that length.
     */
    public static List<Session> fetch(TimetableDraftRepository drafts, UUID termId, Timetable timetable) {
        var unmatched = new ArrayList<>(drafts.findByTermId(termId)
                .map(draft -> draft.getSessions())
                .orElse(List.of()));
        List<Session> sessions = new ArrayList<>();
        for (Offering offering : timetable.getOfferings()) {
            for (int lengthInPeriods : offering.getSessionLengthsInPeriods()) {
                var saved = unmatched.stream()
                        .filter(placed -> placed.getOffering().getId().equals(offering.getId())
                                && placed.getLengthInPeriods() == lengthInPeriods)
                        .findFirst();
                Session session = new Session(saved.map(placed -> placed.getId()).orElseGet(() -> UUID.randomUUID()),
                        offering, lengthInPeriods, timetable.getPeriods(), timetable.getRooms());
                saved.ifPresent(placed -> {
                    unmatched.remove(placed);
                    session.setStartPeriod(timetable.period(placed.getStartPeriod().getId()));
                    session.setRoom(timetable.room(placed.getRoom().getId()));
                    session.setLocked(placed.isLocked());
                });
                sessions.add(session);
            }
        }
        return sessions;
    }

    /** The day it falls on. */
    public DayOfWeek getDay() {
        return startPeriod.getDay();
    }

    /** The number of the period it starts on. */
    public int getFirstPeriodNumber() {
        return startPeriod.getNumber();
    }

    /** The number of the period right after its last one. */
    public int getEndPeriodNumber() {
        return startPeriod.getNumber() + lengthInPeriods;
    }

    /** How many periods it shares with another session: none when they fall on different days. */
    public int periodsSharedWith(Session other) {
        if (getDay() != other.getDay()) {
            return 0;
        }
        int sharedFrom = Math.max(getFirstPeriodNumber(), other.getFirstPeriodNumber());
        int sharedUntil = Math.min(getEndPeriodNumber(), other.getEndPeriodNumber());
        return Math.max(0, sharedUntil - sharedFrom);
    }
}
