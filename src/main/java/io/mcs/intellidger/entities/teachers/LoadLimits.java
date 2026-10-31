package io.mcs.intellidger.entities.teachers;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A teacher's load over a day or over a week, in periods: the most they may teach, which the solver never passes, and
 * the range they'd like to stay within, which it tries to.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class LoadLimits {

    /** The most they may teach. */
    @PositiveOrZero
    @Column(name = "max", nullable = false)
    private int max;

    /** The least they'd like to teach; 0 when they have no such wish. */
    @PositiveOrZero
    @Column(name = "preferred_min", nullable = false)
    private int preferredMin;

    /** The most they'd like to teach; the same as {@link #max} when they have no such wish. */
    @PositiveOrZero
    @Column(name = "preferred_max", nullable = false)
    private int preferredMax;

    @AssertTrue(message = "The preferred load must lie within the maximum: preferred min ≤ preferred max ≤ max.")
    public boolean isInOrder() {
        return preferredMin <= preferredMax && preferredMax <= max;
    }
}
