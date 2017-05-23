package io.smartcat.ranger.rules;

import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for creating random range values of type Short.
 */
public class RangeRuleShort extends RangeRule<Short> {

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List)}.
     *
     * @param rangeMarkers List of shorts that denotes the ranges.
     */
    public RangeRuleShort(List<Short> rangeMarkers) {
        super(rangeMarkers);
    }

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List, Distribution)}.
     *
     * @param rangeMarkers List of shorts that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     */
    public RangeRuleShort(List<Short> rangeMarkers, Distribution distribution) {
        super(rangeMarkers, distribution);
    }

    @Override
    protected Short nextValue(Short lower, Short upper) {
        return (short) distribution.nextLong(lower, upper);
    }

    @Override
    protected Short nextEdgeCase() {
        // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
        // this method will subtract 1 from the end of the range.
        // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
        // therefore ends of the ranges are on odd positions.
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return (short) (rangeEdges.remove(0) - 1);
        }
    }
}
