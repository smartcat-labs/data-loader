package io.smartcat.ranger.rules;

import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for creating random range values.
 */
public class RangeRuleDouble extends RangeRule<Double> {

    /**
     * Small value that will be subtracted from the end of ranges.
     */
    public static final Double EPSILON = 0.00000000001;

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List)}.
     *
     * @param rangeMarkers List of doubles that denotes the ranges.
     */
    public RangeRuleDouble(List<Double> rangeMarkers) {
        super(rangeMarkers);
    }

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List, Distribution)}.
     *
     * @param rangeMarkers List of doubles that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     */
    public RangeRuleDouble(List<Double> rangeMarkers, Distribution distribution) {
        super(rangeMarkers, distribution);
    }

    @Override
    protected Double nextValue(Double lower, Double upper) {
        return distribution.nextDouble(lower, upper);
    }

    @Override
    protected Double nextEdgeCase() {
        // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
        // small value (EPSILON) is subtracted from the end of the range.
        // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
        // therefore ends of the ranges are on odd positions.
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - EPSILON;
        }
    }
}
