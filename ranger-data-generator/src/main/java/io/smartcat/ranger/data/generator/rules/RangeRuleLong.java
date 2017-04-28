package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.data.generator.util.Randomizer;

/***
 * Rule for creating random range values.
 */
public final class RangeRuleLong implements Rule<Long> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Long> ranges;
    private final List<Long> rangeEdges;

    private final Randomizer random;

    private RangeRuleLong(Builder builder) {
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
        this.random = builder.random;
    }

    @Override
    public Long getRandomAllowedValue() {
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

        Long randomValue = random.nextLong(ranges.get(randomRangeIndex * 2), ranges.get((randomRangeIndex * 2) + 1));

        return randomValue;
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract 1 from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Long nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            return rangeEdges.remove(0) - 1;
        }
    }

    /**
     * Builder for RangeRuleLong.
     */
    public static class Builder {

        private List<Long> ranges = new ArrayList<>();
        private final List<Long> rangeEdges = new LinkedList<>();
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
         * @param ranges array of Long that denote the ranges.
         * @return Builder with set ranges of Long.
         */
        public Builder ranges(Long... ranges) {
            this.ranges.addAll(Arrays.asList(ranges));
            this.rangeEdges.addAll(Arrays.asList(ranges));
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleLong object based on the previously instantiated builder.
         */
        public RangeRuleLong build() {
            return new RangeRuleLong(this);
        }

    }

}
