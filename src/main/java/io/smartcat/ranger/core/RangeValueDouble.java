package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates double value within specified range.
 *
 */
public class RangeValueDouble extends RangeValue<Double> {

    /**
     * Constructs range with specified <code>beginning</code> and <code>end</code>. <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     */
    public RangeValueDouble(Double beginning, Double end) {
        super(beginning, end);
    }

    /**
     * Constructs range with specified <code>beginning</code>, <code>end</code> and <code>distribution</code>.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueDouble(Double beginning, Double end, Distribution distribution) {
        super(beginning, end, distribution);
    }

    @Override
    protected void eval() {
        val = distribution.nextDouble(beginning, end);
    }
}
