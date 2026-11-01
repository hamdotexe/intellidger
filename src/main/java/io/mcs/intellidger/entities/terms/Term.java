package io.mcs.intellidger.entities.terms;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * One semester of the department, such as Fall 2026. What changes from semester to semester belongs to a term: its
 * week plan, everyone's blocks and loads, what each section is taught, and its timetables. Teachers, rooms, sections
 * and courses outlive it.
 */
@Entity
@Audited
@Table(name = "terms", uniqueConstraints = @UniqueConstraint(name = "uk_terms_name", columnNames = "name"))
@Getter
@Setter
public class Term {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /** As the department names it, such as "Fall 2026". */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * A term preference, set for every term: whether a lab's instructor must be free for its whole session, beside
     * its lab engineer. When false, only the lab engineer must be; the instructor may teach elsewhere meanwhile.
     */
    @NotNull
    @Column(name = "instructors_attend_labs", nullable = false)
    private Boolean instructorsAttendLabs;

    /**
     * A term preference, set for every term: whether a lab's periods count toward its instructor's teaching load, as
     * they always do toward its lab engineer's. Keeping a lab's time free and teaching in it are two things, so an
     * instructor who attends labs may or may not have them count. One who doesn't attend never does: true while
     * instructors don't attend labs is refused when saved.
     */
    @NotNull
    @Column(name = "labs_count_as_instructor_load", nullable = false)
    private Boolean labsCountAsInstructorLoad;

    /**
     * A term preference, set for every term: whether each student takes just one of their batch's electives. When
     * true, sections of different electives may meet at the same time; when false, they never do.
     */
    @NotNull
    @Column(name = "electives_mutually_exclusive", nullable = false)
    private Boolean electivesMutuallyExclusive;
}
