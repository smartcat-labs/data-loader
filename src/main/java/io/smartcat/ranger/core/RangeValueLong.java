package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates long value within specified range.
 *
 */
public class RangeValueLong extends RangeValue<Long> {

    /**
     * Constructs range with specified <code>beginning</code> and <code>end</code>. <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     */
    public RangeValueLong(Long beginning, Long end) {
        super(beginning, end);
    }

    /**
     * Constructs range with specified <code>beginning</code>, <code>end</code> and <code>distribution</code>.
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueLong(Long beginning, Long end, Distribution distribution) {
        super(beginning, end, distribution);
    }

    @Override
    protected void eval() {
        val = distribution.nextLong(beginning, end);
    }
}
