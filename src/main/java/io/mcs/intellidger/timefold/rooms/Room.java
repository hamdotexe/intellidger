package io.mcs.intellidger.timefold.rooms;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintCollectors;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.rooms.RoomRepository;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A room or a lab, and how many sections it can take in the same period. */
@Getter
@AllArgsConstructor
public class Room {

    private final UUID id;

    private final String name;

    private final int maxSectionsAtOnce;

    /** Every room, as saved: they outlive terms. */
    public static List<Room> fetch(RoomRepository rooms) {
        return rooms.findAll().stream()
                .map(room -> new Room(room.getId(), room.getName(), room.getMaxSectionsAtOnce()))
                .toList();
    }

    /** A room taking more sections in a period than it can at once: hard, for each section over. */
    public static Constraint overfilled(ConstraintFactory factory) {
        return factory.forEach(Session.class)
                .join(Period.class,
                        Joiners.equal(session -> session.getDay(), period -> period.getDay()),
                        Joiners.filtering((session, period) -> session.covers(period)))
                .groupBy((session, period) -> session.getRoom(), (session, period) -> period,
                        ConstraintCollectors.countBi())
                .filter((room, period, sections) -> sections > room.getMaxSectionsAtOnce())
                .penalize(HardSoftScore.ONE_HARD, (room, period, sections) -> sections - room.getMaxSectionsAtOnce())
                .asConstraint("Room with more sections than it takes at once");
    }
}
