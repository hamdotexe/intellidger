package io.mcs.intellidger.timefold.offerings;

import io.mcs.intellidger.repositories.offerings.TheoryOfferingRepository;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.timetables.Timetable;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/** A course's theory for a section, such as the theory of Artificial Intelligence for BESE-30-C. */
public class TheoryOffering extends Offering {

    public TheoryOffering(UUID id, Course course, Section section, List<Integer> sessionLengthsInPeriods,
            List<InstructorPriority> instructors, int instructorPreferenceWeight, List<RoomPriority> rooms,
            int roomPreferenceWeight, Set<Room> impossibleRooms, boolean onlyListedRooms) {
        super(id, course, section, sessionLengthsInPeriods, instructors, instructorPreferenceWeight, rooms,
                roomPreferenceWeight, impossibleRooms, onlyListedRooms);
    }

    /** The term's theory offerings, as saved. */
    public static List<TheoryOffering> fetch(TheoryOfferingRepository offerings, UUID termId, Timetable timetable) {
        return offerings.findByTermId(termId).stream()
                .map(offering -> new TheoryOffering(offering.getId(),
                        timetable.course(offering.getCourse().getId()),
                        timetable.section(offering.getSection().getId()),
                        List.copyOf(offering.getSessionLengthsInPeriods()),
                        offering.getInstructors().stream()
                                .map(listed -> new InstructorPriority(timetable.teacher(listed.getTeacher().getId()),
                                        listed.getPriority()))
                                .toList(),
                        offering.getInstructorPreferenceWeight(),
                        offering.getRooms().stream()
                                .map(listed -> new RoomPriority(timetable.room(listed.getRoom().getId()),
                                        listed.getPriority()))
                                .toList(),
                        offering.getRoomPreferenceWeight(),
                        offering.getImpossibleRooms().stream()
                                .map(room -> timetable.room(room.getId()))
                                .collect(Collectors.toSet()),
                        offering.isOnlyListedRooms()))
                .toList();
    }
}
