package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates long value within specified range.
 *
 */
public class RangeValueLong extends RangeValue<Long> {

    /**
     * Constructs range with specified <code>range</code>. <code>distribution</code> is set to
     * Uniform distribution.
     *
     * @param range Long range.
     */
    public RangeValueLong(Range<Long> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>distribution</code>.
     *
     * @param range Long range.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueLong(Range<Long> range, Distribution distribution) {
        super(range, distribution);
    }

    @Override
    protected void eval() {
        val = distribution.nextLong(beginning, end);
    }
}
