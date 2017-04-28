package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.data.generator.util.Randomizer;

/**
 * Rule for creating random range values of type Short.
 */
public class RangeRuleShort implements Rule<Short> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Short> ranges;
    private final List<Short> rangeEdges;

    private final Randomizer random;

    private RangeRuleShort(Builder builder) {
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
        this.random = builder.random;
    }

    @Override
    public Short getRandomAllowedValue() {
        if (!rangeEdges.isEmpty()) {
            return nextEdgeCase();
        }

        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }

        Short randomValue = (short) random.nextLong(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));

        return randomValue;
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract 1 from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Short nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return (short) (rangeEdges.remove(0) - 1);
        }
    }

    /**
     * Builder for RangeRuleShort.
     */
    public static class Builder {

        private List<Short> ranges = new ArrayList<>();
        private final List<Short> rangeEdges = new LinkedList<>();
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
         * @param ranges array of Short that denote the ranges.
         * @return Builder with set ranges of Short.
         */
        public Builder ranges(Short... ranges) {
            this.ranges.addAll(Arrays.asList(ranges));
            this.rangeEdges.addAll(Arrays.asList(ranges));
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleShort object based on the previously instantiated builder.
         */
        public RangeRuleShort build() {
            return new RangeRuleShort(this);
        }

    }

}
