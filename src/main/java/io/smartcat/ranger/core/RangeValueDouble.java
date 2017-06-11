package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates double value within specified range.
 *
 */
public class RangeValueDouble extends RangeValue<Double> {

    /**
     * Constructs range with specified <code>range</code>, <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param range Double range.
     */
    public RangeValueDouble(Range<Double> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>distribution</code>.
     *
     * @param range Double range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueDouble(Range<Double> range, Distribution distribution) {
        super(range, distribution);
    }

    @Override
    protected void eval() {
        val = distribution.nextDouble(beginning, end);
    }
}
