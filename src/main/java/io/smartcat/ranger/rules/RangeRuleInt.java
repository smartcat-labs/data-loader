package io.smartcat.ranger.rules;

import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for creating random range values of type Integer.
 */
public class RangeRuleInt extends RangeRule<Integer> {

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List)}.
     *
     * @param rangeMarkers List of integers that denotes the ranges.
     */
    public RangeRuleInt(List<Integer> rangeMarkers) {
        super(rangeMarkers);
    }

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List, Distribution)}.
     *
     * @param rangeMarkers List of integers that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     */
    public RangeRuleInt(List<Integer> rangeMarkers, Distribution distribution) {
        super(rangeMarkers, distribution);
    }

    @Override
    protected Integer nextValue(Integer lower, Integer upper) {
        return (int) distribution.nextLong(lower, upper);
    }

    @Override
    protected Integer nextEdgeCase() {
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
