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
     * Constructs range with specified <code>beginning</code> and <code>end</code>. <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     */
    public RangeValue(T beginning, T end) {
        this(beginning, end, new UniformDistribution());
    }

    /**
     * Constructs range with specified <code>beginning</code>, <code>end</code> and <code>distribution</code>.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValue(T beginning, T end, Distribution distribution) {
        checkRangeInput(beginning, end);
        if (distribution == null) {
            throw new IllegalArgumentException("Distribution cannot be null.");
        }
        this.beginning = beginning;
        this.end = end;
        this.distribution = distribution;
    }

    private void checkRangeInput(T beginning, T end) {
        if (beginning == null) {
            throw new InvalidRangeBoundsException("Beginning of the range cannot be null.");
        }
        if (end == null) {
            throw new InvalidRangeBoundsException("End of the range cannot be null.");
        }
        if (end.compareTo(beginning) <= 0) {
            throw new InvalidRangeBoundsException("End of the range must be greater than the beginning of the range.");
        }
    }
}
