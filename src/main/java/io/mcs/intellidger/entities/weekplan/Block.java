package io.mcs.intellidger.entities.weekplan;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.Audited;

/**
 * One period marked on the week of a teacher, a room or a section. A period with no block is open. Every owner's
 * blocks come in two kinds: hard, never used; and soft, used only at the cost the block names.
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
}
