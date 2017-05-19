package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import io.smartcat.ranger.data.generator.distribution.Distribution;

/***
 * Rule for creating random range values.
 */
public final class RangeRuleLong extends RangeRule<Long> {

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List)}.
     *
     * @param rangeMarkers List of longs that denotes the ranges.
     */
    public RangeRuleLong(List<Long> rangeMarkers) {
        super(rangeMarkers);
    }

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List, Distribution)}.
     *
     * @param rangeMarkers List of longs that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     */
    public RangeRuleLong(List<Long> rangeMarkers, Distribution distribution) {
        super(rangeMarkers, distribution);
    }

    @Override
    protected Long nextValue(Long lower, Long upper) {
        return distribution.nextLong(lower, upper);
    }

    @Override
    protected Long nextEdgeCase() {
        // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
        // 1 is subtracted from the end of the range.
        // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
        // therefore ends of the ranges are on odd positions.
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - 1;
        }
    }
}
