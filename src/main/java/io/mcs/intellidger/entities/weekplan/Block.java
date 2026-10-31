package io.mcs.intellidger.entities.weekplan;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * One period marked on someone's or something's week: hard when it can't be used, soft when it would rather not be.
 * A period with no block is open. Whose week it is belongs to the entity that extends this: a teacher's, a room's or a
 * section's.
 */
@MappedSuperclass
@Audited
@Getter
@Setter
public abstract class Block {

    /** Its term is the period's. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "period_id", nullable = false, updatable = false)
    private Period period;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "strength", nullable = false)
    private BlockStrength strength;
}
