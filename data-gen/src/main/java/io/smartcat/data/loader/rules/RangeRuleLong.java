package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.smartcat.data.loader.RangeUtil;
import io.smartcat.data.loader.util.Randomizer;

/***
 * Rule for creating random range values.
 */
public final class RangeRuleLong implements Rule<Long> {

    private boolean exclusive;

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private List<Long> ranges = new ArrayList<>();

    private Randomizer random;

    private RangeRuleLong() {
    };

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return RangeRuleLong with set Randomizer.
     */
    public RangeRuleLong withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
     *
     * @param rangeMarkers array of longs that denote the ranges.
     * @return RangeRuleLong with set ranges.
     */
    public static RangeRuleLong withRanges(Long... rangeMarkers) {
        RangeRuleLong result = new RangeRuleLong();

        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    /**
     * Set exclusive range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule, meaning that only builder that uses this
     * instance of the rule can create value for the property in these ranges.
     *
     * @param rangeMarkers array of longs that denote the exclusive ranges.
     * @return exclusive RangeRuleLong with set ranges.
     */
    public static RangeRuleLong withRangesX(Long... rangeMarkers) {
        RangeRuleLong result = new RangeRuleLong();

        result.exclusive = true;
        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    /**
     * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
     *
     * @param rangeMarkers list of longs that denote the ranges.
     * @return RangeRuleLong with set ranges.
     */
    public static RangeRuleLong withRanges(List<Long> rangeMarkers) {
        RangeRuleLong result = new RangeRuleLong();

        result.ranges.addAll(rangeMarkers);

        return result;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    private List<Long> getAllowedRanges() {
        return ranges;
    }

    @Override
    public Rule<Long> recalculatePrecedance(Rule<?> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
        }
        if (!(exclusiveRule instanceof RangeRuleLong)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }
        RangeRuleLong otherRule = (RangeRuleLong) exclusiveRule;

        if (!RangeUtil.rangesIntersects(this.ranges, otherRule.getAllowedRanges())) {
            return this;
        }
        List<Long> newRanges = RangeUtil.recalculateRanges(this.ranges, otherRule.getAllowedRanges());

        return RangeRuleLong.withRanges(newRanges).withRandom(random);
    }

    @Override
    public Long getRandomAllowedValue() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1
        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = random.nextInt(ranges.size() / 2);
        }

        Long randomValue = random.nextLong(ranges.get(randomRangeIndex * 2), ranges.get((randomRangeIndex * 2) + 1));

        return randomValue;
    }

}
