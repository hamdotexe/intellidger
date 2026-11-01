package io.mcs.intellidger.entities.teachers;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.UUID;

/**
 * Someone who teaches: a lecturer, or a lab engineer, who is scheduled exactly like one. Kept across terms; how much
 * they may teach in a term is their teaching load.
 */
@Entity
@Audited
@Table(name = "teachers", uniqueConstraints = @UniqueConstraint(name = "uk_teachers_name", columnNames = "name"))
@Getter
@Setter
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    /**
     * As timetables print it, rank or title first, such as "Dr Abdul Razzaq" or "LE Aqsa". No two teachers share one,
     * so it tells them apart on every screen and export.
     */
    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
}
