package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating random range values.
 */
public class RangeRuleFloat implements Rule<Float> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Float> ranges;

    private final List<Float> rangeEdges;

    private final Randomizer random;

    /**
     * Small value that will be subtracted from the end of ranges.
     */
    public static final Float EPSILON = 0.00001f;

    private RangeRuleFloat(Builder builder) {
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
        this.random = builder.random;
    }

    @Override
    public Float getRandomAllowedValue() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1

        if (!rangeEdges.isEmpty()) {
            return nextEdgeCase();
        }

        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }
        float randomFloat = (float) random.nextDouble(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));

        return randomFloat;
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract small value (EPSILON) from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Float nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - EPSILON;
        }
    }

    /**
     * Builder for RangeRuleFloat.
     */
    public static class Builder {

        private List<Float> ranges = new ArrayList<>();
        private final List<Float> rangeEdges = new LinkedList<>();
        private Randomizer random;

        /**
         * Constructor.
         *
         * @param randomizer Randomizer implementation.
         */
        public Builder(Randomizer randomizer) {
            this.random = randomizer;
        }

        /**
         * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
         *
         * @param ranges array of Float that denote the ranges.
         * @return Builder with set ranges of Float.
         */
        public Builder ranges(Float...ranges) {
            this.ranges.addAll(Arrays.asList(ranges));
            this.rangeEdges.addAll(Arrays.asList(ranges));
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleFloat object based on the previously instantiated builder.
         */
        public RangeRuleFloat build() {
            return new RangeRuleFloat(this);
        }

    }

}
