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
    protected T beginning;

    /**
     * End of the range.
     */
    protected T end;

    /**
     * Distribution to use.
     */
    protected Distribution distribution;

    /**
     * Constructs range value with specified <code>range</code>. <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param range Range.
     */
    public RangeValue(Range<T> range) {
        this(range, new UniformDistribution());
    }

    /**
     * Constructs range value with specified <code>range</code> and <code>distribution</code>.
     *
     * @param range Range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValue(Range<T> range, Distribution distribution) {
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
        this.distribution = distribution;
    }
}
