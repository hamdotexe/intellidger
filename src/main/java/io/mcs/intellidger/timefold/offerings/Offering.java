package io.mcs.intellidger.timefold.offerings;

import io.mcs.intellidger.timefold.courses.Course;
import io.mcs.intellidger.timefold.rooms.Room;
import io.mcs.intellidger.timefold.sections.Section;
import io.mcs.intellidger.timefold.teachers.Teacher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * A course's theory or its lab for a section: its sessions, who may teach it and where. Which of its instructors
 * teaches it, and when and where its sessions fall, are what the solver decides.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Offering {

    private final UUID id;

    private final Course course;

    private final Section section;

    /** How long each of its sessions is, in periods, exactly as entered: 2 and 1 for a 2+1. */
    private final List<Integer> sessionLengthsInPeriods;

    /** Who may teach it, by priority; nobody else ever does. */
    private final List<InstructorPriority> instructors;

    /** What each step its instructor stands below the first priority costs. */
    private final int instructorPreferenceWeight;

    /** The rooms it would like, by priority. */
    private final List<RoomPriority> rooms;

    /** What each step a session's room stands below the first priority costs, an unlisted room one below the last. */
    private final int roomPreferenceWeight;

    private final Set<Room> impossibleRooms;

    /** "Only these rooms": no room but the listed ones. */
    private final boolean onlyListedRooms;

    /** What its instructor costs: its instructor weight for each step the teacher stands below the first. */
    public int instructorCost(Teacher teacher) {
        return instructorPreferenceWeight * stepsDown(instructors, teacher);
    }

    /** What a session's room costs: its room weight for each step below the first, an unlisted room below the last. */
    public int roomCost(Room room) {
        var own = rooms.stream()
                .filter(listed -> listed.getRoom().equals(room))
                .mapToInt(listed -> listed.getPriority())
                .min();
        long steps = rooms.stream()
                .mapToInt(listed -> listed.getPriority())
                .filter(priority -> own.isEmpty() || priority < own.getAsInt())
                .distinct()
                .count();
        return roomPreferenceWeight * (int) steps;
    }

    /** How many priorities among these come before the teacher's: 0 for one of the first. */
    static int stepsDown(List<InstructorPriority> listed, Teacher teacher) {
        int own = listed.stream()
                .filter(entry -> entry.getTeacher().equals(teacher))
                .mapToInt(entry -> entry.getPriority())
                .min()
                .orElseThrow();
        return (int) listed.stream()
                .mapToInt(entry -> entry.getPriority())
                .filter(priority -> priority < own)
                .distinct()
                .count();
    }
}
