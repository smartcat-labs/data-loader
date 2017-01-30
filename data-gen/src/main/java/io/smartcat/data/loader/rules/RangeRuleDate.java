package io.smartcat.data.loader.rules;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating random range values.
 */
public class RangeRuleDate implements Rule<Date> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Date> ranges;
    private final List<Date> rangeEdges;

    private Randomizer random;

    private RangeRuleDate(Builder builder) {
        this.random = builder.random;
        this.ranges = builder.ranges;
        this.rangeEdges = builder.rangeEdges;
    };

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return RangeRuleDate with set Randomizer.
     */
    public RangeRuleDate withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    @Override
    public Date getRandomAllowedValue() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1

        if (!rangeEdges.isEmpty()) {
            return nextEdgeCase();
        }

        // generate random values
        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }
        Long randomValue = random.nextLong(ranges.get(randomRangeIndex * 2).getTime(),
                ranges.get((randomRangeIndex * 2) + 1).getTime());
        Instant randomInstant = Instant.ofEpochMilli(randomValue);

        return Date.from(randomInstant);
    }

    // Since the definition of the range is inclusive at the beginning and end of the range is exclusive,
    // this method will subtract one millisecond from the end of the range.
    // Ranges are defined with a list and there can be several ranges defined in one list, e.g. [a,b),[c,d),[e,f),
    // therefore ends of the ranges are on odd positions.
    private Date nextEdgeCase() {
        if (rangeEdges.size() % 2 == 0) {
            return rangeEdges.remove(0);
        } else {
            long edge = rangeEdges.remove(0).getTime() - 1;
            Instant largestInstant = Instant.ofEpochMilli(edge);
            return Date.from(largestInstant);
        }
    }

    /**
     * Builder for RangeRuleDate.
     */
    public static class Builder {

        private List<Date> ranges = new ArrayList<>();
        private List<Date> rangeEdges = new ArrayList<>();

        private Randomizer random;

        /**
         * Constructor.
         *
         * @param random Randomizer implementation.
         */
        public Builder(Randomizer random) {
            this.random = random;
        }

        /**
         * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
         *
         * @param dates array of Dates that denote the ranges.
         * @return Builder with set ranges of dates.
         */
        public Builder ranges(Date...dates) {
            List<Date> copy = new LinkedList<>();
            for (Date date : dates) {
                copy.add(new Date(date.getTime()));
            }
            ranges.addAll(copy);
            rangeEdges.addAll(copy);
            return this;
        }

        /**
         * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
         *
         * @param dates List of Dates that denote the ranges.
         * @return Builder with set ranges of dates.
         */
        public Builder ranges(List<Date> dates) {
            List<Date> copy = new LinkedList<>();
            for (Date date : dates) {
                copy.add(new Date(date.getTime()));
            }
            ranges.addAll(copy);
            rangeEdges.addAll(copy);
            return this;
        }

        /**
         * Build method.
         *
         * @return immutable RangeRuleDate object based on the previously instantiated builder.
         */
        public RangeRuleDate build() {
            return new RangeRuleDate(this);
        }
    }
}
