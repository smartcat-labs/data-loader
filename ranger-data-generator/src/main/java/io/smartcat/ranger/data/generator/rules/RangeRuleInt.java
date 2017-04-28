package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.data.generator.util.Randomizer;

/**
 * Rule for creating random range values of type Integer.
 */
public class RangeRuleInt implements Rule<Integer> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Integer> ranges;
    private final List<Integer> rangeEdges;

    private final Randomizer random;

    private RangeRuleInt(Builder builder) {
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
        this.random = builder.random;
    }

    @Override
    public Integer getRandomAllowedValue() {
        if (!rangeEdges.isEmpty()) {
            return nextEdgeCase();
        }

        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }

        int randomValue = (int) random.nextLong(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));

        return randomValue;
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract 1 from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Integer nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - 1;
        }
    }

    /**
     * Builder for RangeRuleInt.
     */
    public static class Builder {

        private List<Integer> ranges = new ArrayList<>();
        private final List<Integer> rangeEdges = new LinkedList<>();
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
         * @param ranges array of Integers that denote the ranges.
         * @return Builder with set ranges of Integer.
         */
        public Builder ranges(Integer... ranges) {
            this.ranges.addAll(Arrays.asList(ranges));
            this.rangeEdges.addAll(Arrays.asList(ranges));
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleInt object based on the previously instantiated builder.
         */
        public RangeRuleInt build() {
            return new RangeRuleInt(this);
        }

    }

}
