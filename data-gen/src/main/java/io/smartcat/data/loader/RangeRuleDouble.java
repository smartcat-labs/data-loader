package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating random range values.
 */
public class RangeRuleDouble implements Rule<Double> {

    private boolean exclusive;

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private List<Double> ranges = new ArrayList<>();

    private Randomizer random;

    private RangeRuleDouble() {
    };

    public RangeRuleDouble withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    public static RangeRuleDouble withRanges(Double... rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    public static RangeRuleDouble withRangesX(Double... rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.exclusive = true;
        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    public static RangeRuleDouble withRanges(List<Double> rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.ranges.addAll(rangeMarkers);

        return result;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    private List<Double> getAllowedRanges() {
        return ranges;
    }

    @Override
    public Rule<Double> recalculatePrecedance(Rule<Double> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
        }
        if (!(exclusiveRule instanceof RangeRuleDouble)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }
        RangeRuleDouble otherRule = (RangeRuleDouble) exclusiveRule;

        if (!RangeUtil.rangesIntersects(this.ranges, otherRule.getAllowedRanges())) {
            return this;
        }
        List<Double> newRanges = RangeUtil.recalculateRanges(this.ranges, otherRule.getAllowedRanges());

        return RangeRuleDouble.withRanges(newRanges).withRandom(random);
    }

    @Override
    public Double getRandomAllowedValue() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1
        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }
        Double randomDouble = random.nextDouble(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));

        return randomDouble;
    }

}
