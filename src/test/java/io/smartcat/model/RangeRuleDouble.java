package io.smartcat.model;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.assertj.core.util.Lists;

public class RangeRuleDouble implements Rule<Double> {

    private boolean exclusive;

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private List<Double> ranges = Lists.newArrayList();

    private RangeRuleDouble() {
    };

    public static RangeRuleDouble withRanges(Double... rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.ranges.addAll(Lists.newArrayList(rangeMarkers));

        return result;
    }

    public static RangeRuleDouble withRangesX(Double... rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.exclusive = true;
        result.ranges.addAll(Lists.newArrayList(rangeMarkers));

        return result;
    }

    public static RangeRuleDouble withRanges(List<Double> rangeMarkers) {
        RangeRuleDouble result = new RangeRuleDouble();

        result.ranges.addAll(Lists.newArrayList(rangeMarkers));

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

        if (!rangesIntersects(this.ranges, otherRule.getAllowedRanges())) {
            return this;
        }
        List<Double> newRanges = recalculateRanges(otherRule.getAllowedRanges());

        return RangeRuleDouble.withRanges(newRanges);
    }

    private boolean rangesIntersects(List<Double> range1, List<Double> range2) {
        return range1.get(0) <= range2.get(1) && range2.get(0) <= range1.get(1);
    }

    private List<Double> recalculateRanges(List<Double> exclusiveRanges) {

        Double x1 = this.ranges.get(0);
        Double x2 = this.ranges.get(1);
        Double y1 = exclusiveRanges.get(0);
        Double y2 = exclusiveRanges.get(1);

        if (y1 <= x1 && x2 <= y2) { // 1.
            // ----x1----------x2----
            // -y1---------------y2--
            // -y1-------------y1----
            // ----y1------------y2--
            // ----y1----------y2----
            return Lists.newArrayList();
        }

        if (x1 < y1 && y2 < x2) { // 2.
            // ----x1----------x2----
            // --------y1--y2--------
            return Lists.newArrayList(x1, y1, y2, x2);
        }

        if (y1 <= x1) { // x2 > y2, otherwise 1.
            // ----x1----------x2----
            // -y1--------y2--------- <
            // ----y1-----y2--------- =
            return Lists.newArrayList(y2, x2);
        }

        if (x2 <= y2) { // x1 < y1 otherwise 1.
            // ----x1----------x2----
            // --------y1--------y2--
            return Lists.newArrayList(x1, y1);
        }

        throw new IllegalStateException("Unexpected error: x1=" + x1 + ", x2=" + x2 + ", y1=" + y1 + ", y2=" + y2);
    }

    @Override
    public Double getRandomAllowedValue() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1
        int randomRangeIndex = 0;
        if (ranges.size() > 2) {
            randomRangeIndex = ThreadLocalRandom.current().nextInt(0, ranges.size() / 2);
        }
        System.out.println("size is: " + ranges.size());
        System.out.println("randomRangeIdex is: " + randomRangeIndex);

        // randomRangeIndex == 0 => index1 = 0, index2 = 1;
        // randomRangeIndex == 1 => index1 = 2, index2 = 3;
        // randomRangeIndex == 2 => index1 = 4, index2 = 5;
        // randomRangeIndex == 3 => index1 = 6, index2 = 7;
        Double randomBirthDate = ThreadLocalRandom.current().nextDouble(ranges.get(randomRangeIndex * 2),
                ranges.get((randomRangeIndex * 2) + 1));
        // if we used generic type <T> instead of Long there would be no way to get random of type T because we do not
        // know what is the type T

        return randomBirthDate;
    }

}
