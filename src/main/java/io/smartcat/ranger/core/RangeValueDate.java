package io.smartcat.ranger.core;

import java.util.Date;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Randomly generates {@link Date} value within specified range.
 */
public class RangeValueDate extends RangeValue<Date> {

    private boolean beginningEdgeCaseUsed = false;
    private boolean endEdgeCaseUsed = false;

    /**
     * Constructs range with specified <code>range</code>.
     *
     * @param range Double range.
     */
    public RangeValueDate(Range<Date> range) {
        super(range);
    }

    /**
     * Constructs range with specified <code>range</code> and <code>useEdgeCases</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     */
    public RangeValueDate(Range<Date> range, boolean useEdgeCases) {
        super(range, useEdgeCases);
    }

    /**
     * Constructs range with specified <code>range</code>, <code>useEdgeCases</code> and <code>distribution</code>.
     *
     * @param range Double range.
     * @param useEdgeCases Indicates whether to create edge cases as first two values or not.
     * @param distribution Distribution to use for value selection.
     */
    public RangeValueDate(Range<Date> range, boolean useEdgeCases, Distribution distribution) {
        super(range, useEdgeCases, distribution);
    }

    @Override
    protected void eval() {
        if (useEdgeCases && !beginningEdgeCaseUsed) {
            beginningEdgeCaseUsed = true;
            val = new Date(beginning.getTime());
            return;
        }
        if (useEdgeCases && !endEdgeCaseUsed) {
            endEdgeCaseUsed = true;
            val = new Date(end.getTime() - 1);
            return;
        }
        val = new Date(distribution.nextLong(beginning.getTime(), end.getTime()));
    }
}
