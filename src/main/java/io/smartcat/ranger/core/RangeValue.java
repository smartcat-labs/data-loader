package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

/**
 * Base range value class. Randomly generates value within specified range.
 *
 * @param <T> Type this value would evaluate to.
 */
public abstract class RangeValue<T extends Comparable<T>> extends Value<T> {

    /**
     * Beginning value of the range.
     */
    protected final T beginning;

    /**
     * End of the range.
     */
    protected final T end;

    /**
     * Indicates whether to create edge cases as first two values or not.
     */
    protected final boolean useEdgeCases;

    /**
     * Distribution to use.
     */
    protected final Distribution distribution;

    /**
     * Constructs range value with specified <code>range</code>. <code>useEdgeCases</code> is set to
     * <code>true</code> and <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param range Range.
     */
    public RangeValue(Range<T> range) {
        this(range, true);
    }

    /**
     * Constructs range value with specified <code>range</code> and <code>useEdgeCases</code>.
     * <code>distribution</code> is set to {@link UniformDistribution}.
     *
     * @param range Range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValue(Range<T> range, boolean useEdgeCases) {
        this(range, useEdgeCases, new UniformDistribution());
    }

    /**
     * Constructs range value with specified <code>range</code>, <code>useEdgeCases</code> and
     * <code>distribution</code>.
     *
     * @param range Range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValue(Range<T> range, boolean useEdgeCases, Distribution distribution) {
        if (range == null) {
            throw new IllegalArgumentException("Range cannot be null.");
        }
        if (!range.isIncreasing()) {
            throw new InvalidRangeBoundsException("End of the range must be greater than the beginning of the range.");
        }
        if (distribution == null) {
            throw new IllegalArgumentException("Distribution cannot be null.");
        }
        this.beginning = range.getBeginning();
        this.end = range.getEnd();
        this.useEdgeCases = useEdgeCases;
        this.distribution = distribution;
    }
}
