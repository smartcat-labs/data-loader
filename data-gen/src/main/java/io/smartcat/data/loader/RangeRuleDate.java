package io.smartcat.data.loader;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Rule for creating random range values.
 */
public class RangeRuleDate implements Rule<Date> {

    private boolean exclusive;

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private List<Date> ranges = new ArrayList<>();

    private RangeRuleDate() {
    };

    public static RangeRuleDate withRanges(Date... rangeMarkers) {
        RangeRuleDate result = new RangeRuleDate();

        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    public static RangeRuleDate withRangesX(Date... rangeMarkers) {
        RangeRuleDate result = new RangeRuleDate();

        result.exclusive = true;
        result.ranges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    public static RangeRuleDate withRanges(List<Date> rangeMarkers) {
        RangeRuleDate result = new RangeRuleDate();

        result.ranges.addAll(rangeMarkers);

        return result;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    @Override
    public Rule<Date> recalculatePrecedance(Rule<Date> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
        }
        if (!(exclusiveRule instanceof RangeRuleDate)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }
        RangeRuleDate otherRule = (RangeRuleDate) exclusiveRule;

        if (!RangeUtil.rangesIntersects(this.ranges, otherRule.getAllowedRanges())) {
            return this;
        }
        List<Date> newRanges = RangeUtil.recalculateRanges(this.ranges, otherRule.getAllowedRanges());

        return RangeRuleDate.withRanges(newRanges);
    }

    private List<Date> getAllowedRanges() {
        return ranges;
    }

    @Override
    public Date getRandomAllowedValue() {
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

        Long randomBirthDateTimestamp = ThreadLocalRandom.current().nextLong(ranges.get(randomRangeIndex * 2).getTime(),
                ranges.get((randomRangeIndex * 2) + 1).getTime());
        // if we used generic type <T> instead of Long there would be no way to get random of type T because we do not
        // know what is the type T

        Instant birtDateInstant = Instant.ofEpochMilli(randomBirthDateTimestamp);

        return Date.from(birtDateInstant);
    }

}
