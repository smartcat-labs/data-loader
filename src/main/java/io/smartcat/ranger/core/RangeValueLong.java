package io.smartcat.ranger.core;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates long value within specified range.
 */
public class RangeValueLong extends RangeValue<Long> {

    private boolean beginningEdgeCaseUsed = false;
    private boolean endEdgeCaseUsed = false;

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Long range.
     */
    public RangeValueLong(Range<Long> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Long range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueLong(Range<Long> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Long range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueLong(Range<Long> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    @Override
    protected void eval() {
        if (useEdgeCases && !beginningEdgeCaseUsed) {
            beginningEdgeCaseUsed = true;
            val = beginning;
            return;
        }
        if (useEdgeCases && !endEdgeCaseUsed) {
            endEdgeCaseUsed = true;
            val = end - 1;
            return;
        }
        val = distribution.nextLong(beginning, end);
    }
}
