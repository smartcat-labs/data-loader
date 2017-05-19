package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import io.smartcat.ranger.data.generator.distribution.Distribution;

/**
 * Rule for creating random range values.
 */
public class RangeRuleFloat extends RangeRule<Float> {

    /**
     * Small value that will be subtracted from the end of ranges.
     */
    public static final Float EPSILON = 0.00001f;


    /**
     * For documentation, look at {@link RangeRule#RangeRule(List)}.
     *
     * @param rangeMarkers List of floats that denotes the ranges.
     */
    public RangeRuleFloat(List<Float> rangeMarkers) {
        super(rangeMarkers);
    }

    /**
     * For documentation, look at {@link RangeRule#RangeRule(List, Distribution)}.
     *
     * @param rangeMarkers List of floats that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     */
    public RangeRuleFloat(List<Float> rangeMarkers, Distribution distribution) {
        super(rangeMarkers, distribution);
    }

    @Override
    protected Float nextValue(Float lower, Float upper) {
        return (float) distribution.nextDouble(lower, upper);
    }

    @Override
    protected Float nextEdgeCase() {
        // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
        // small value (EPSILON) is subtracted  from the end of the range.
        // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
        // therefore ends of the ranges are on odd positions.
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - EPSILON;
        }
    }
}
