package io.mcs.intellidger.timefold.offerings;

import io.mcs.intellidger.repositories.offerings.LabOfferingRepository;
import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.teachers.Teacher;
import io.mcs.intellidger.timefold.timetables.Timetable;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A course's lab for a section. Its sessions need its lab engineer free for the whole session, and its instructor too
 * when the term says instructors attend labs.
 */
@Getter
public class LabOffering extends Offering {

    /** Who may be its lab engineer, by priority; nobody else ever is. */
    private final List<InstructorPriority> labEngineers;

    /** What each step its lab engineer stands below the first priority costs. */
    private final int labEngineerPreferenceWeight;

    public LabOffering(UUID id, Course course, Section section, List<Integer> sessionLengthsInPeriods,
            List<InstructorPriority> instructors, int instructorPreferenceWeight, List<RoomPriority> rooms,
            int roomPreferenceWeight, Set<Room> impossibleRooms, boolean onlyListedRooms,
            List<InstructorPriority> labEngineers, int labEngineerPreferenceWeight) {
        super(id, course, section, sessionLengthsInPeriods, instructors, instructorPreferenceWeight, rooms,
                roomPreferenceWeight, impossibleRooms, onlyListedRooms);
        this.labEngineers = labEngineers;
        this.labEngineerPreferenceWeight = labEngineerPreferenceWeight;
    }

    /** The term's lab offerings, as saved. */
    public static List<LabOffering> fetch(LabOfferingRepository offerings, UUID termId, Timetable timetable) {
        return offerings.findByTermId(termId).stream()
                .map(offering -> new LabOffering(offering.getId(),
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
                        offering.isOnlyListedRooms(),
                        offering.getLabEngineers().stream()
                                .map(listed -> new InstructorPriority(timetable.teacher(listed.getTeacher().getId()),
                                        listed.getPriority()))
                                .toList(),
                        offering.getLabEngineerPreferenceWeight()))
                .toList();
    }

    /** What its lab engineer costs: its lab engineer weight for each step the teacher stands below the first. */
    public int labEngineerCost(Teacher teacher) {
        return labEngineerPreferenceWeight * stepsDown(labEngineers, teacher);
    }
}
