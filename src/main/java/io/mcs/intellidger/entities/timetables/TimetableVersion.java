package io.mcs.intellidger.entities.timetables;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.time.Instant;

/**
 * A timetable saved under a label, which never changes: what exports and earlier choices are made from. Only a
 * timetable that breaks no hard rule is ever saved, so it keeps just its soft score, the one the solver gave it;
 * reopened after the inputs have changed, it is checked again.
 */
@Entity
@Audited
@DiscriminatorValue("VERSION")
@Table(name = "timetable_versions")
@Getter
@Setter
public class TimetableVersion extends Timetable {

    /** What the coordinator called it, printed on exports, such as "Final" or "After Dr Rabia's change". */
    @NotBlank
    @Column(name = "label", nullable = false)
    private String label;

    @NotNull
    @Column(name = "saved_at", nullable = false, updatable = false)
    private Instant savedAt;

    /** 0 when every preference is met; below 0 by what the ones missed cost. */
    @NotNull
    @Column(name = "soft_score", nullable = false, updatable = false)
    private Integer softScore;
}
