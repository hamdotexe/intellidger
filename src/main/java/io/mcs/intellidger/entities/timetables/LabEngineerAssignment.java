package io.mcs.intellidger.entities.timetables;

import io.mcs.intellidger.entities.offerings.LabOffering;
import io.mcs.intellidger.entities.teachers.Teacher;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/** Who is a lab offering's lab engineer in a timetable: one of its lab engineers, for every one of its sessions. */
@Entity
@Audited
@Table(name = "lab_engineer_assignments", uniqueConstraints = @UniqueConstraint(
        name = "uk_lab_engineer_assignments_timetable_offering", columnNames = {"timetable_id", "offering_id"}))
@Getter
@Setter
public class LabEngineerAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timetable_id", nullable = false, updatable = false)
    private Timetable timetable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "offering_id", nullable = false, updatable = false)
    private LabOffering offering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;
}
