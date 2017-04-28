package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.data.generator.util.Randomizer;

/**
 * Rule for creating random range values.
 */
public class RangeRuleDouble implements Rule<Double> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Double> ranges;

    private final List<Double> rangeEdges;

    private Randomizer random;

    /**
     * Small value that will be subtracted from the end of ranges.
     */
    public static final Double EPSILON = 0.00000000001;

    private RangeRuleDouble(Builder builder) {
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
        this.random = builder.random;
    }

    @Override
    public Double getRandomAllowedValue() {
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
        Double randomDouble = random.nextDouble(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));

        return randomDouble;
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract small value (EPSILON) from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Double nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - EPSILON;
        }
    }

    /**
     * Builder for RangeRuleDouble.
     */
    public static class Builder {

        private List<Double> ranges = new ArrayList<>();
        private final List<Double> rangeEdges = new LinkedList<>();
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
         * @param ranges array of Double that denote the ranges.
         * @return Builder with set ranges of Doubles.
         */
        public Builder ranges(Double...ranges) {
            this.ranges.addAll(Arrays.asList(ranges));
            this.rangeEdges.addAll(Arrays.asList(ranges));
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleDouble object based on the previously instantiated builder.
         */
        public RangeRuleDouble build() {
            return new RangeRuleDouble(this);
        }

    }

}
