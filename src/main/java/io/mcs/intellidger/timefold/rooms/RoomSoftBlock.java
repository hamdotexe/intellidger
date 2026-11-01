package io.mcs.intellidger.timefold.rooms;

import ai.timefold.solver.core.api.score.HardSoftScore;
import ai.timefold.solver.core.api.score.stream.Constraint;
import ai.timefold.solver.core.api.score.stream.ConstraintFactory;
import ai.timefold.solver.core.api.score.stream.Joiners;
import io.mcs.intellidger.repositories.rooms.RoomSoftBlockRepository;
import io.mcs.intellidger.timefold.timetables.Session;
import io.mcs.intellidger.timefold.timetables.Timetable;
import io.mcs.intellidger.timefold.weekplan.Period;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/** A period a room would rather not be used in: a session may use it, at the cost of its weight. */
@Getter
public class RoomSoftBlock extends RoomBlock {

    /** What a timetable pays when a session uses the period anyway. */
    private final int weight;

    public RoomSoftBlock(Period period, Room room, int weight) {
        super(period, room);
        this.weight = weight;
    }

    /** The term's blocks of this kind, as saved. */
    public static List<RoomSoftBlock> fetch(RoomSoftBlockRepository blocks, UUID termId, Timetable timetable) {
        return blocks.findByPeriodTermId(termId).stream()
                .map(block -> new RoomSoftBlock(timetable.period(block.getPeriod().getId()),
                        timetable.room(block.getRoom().getId()), block.getWeight()))
                .toList();
    }

    /** A session in its room's soft block: soft, at its weight. */
    public static Constraint used(ConstraintFactory factory) {
        return factory.forEach(Session.class)
                .join(RoomSoftBlock.class,
                        Joiners.equal(session -> session.getRoom(), block -> block.getRoom()),
                        Joiners.filtering((session, block) -> session.covers(block.getPeriod())))
                .penalize(HardSoftScore.ONE_SOFT, (session, block) -> block.getWeight())
                .asConstraint("Room used in its soft block");
    }
}
