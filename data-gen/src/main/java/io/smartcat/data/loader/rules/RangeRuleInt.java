package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating random range values of type Integer.
 */
public class RangeRuleInt implements Rule<Integer> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Integer> ranges = new ArrayList<>();
    private final List<Integer> rangeEdges = new LinkedList<>();

    private Randomizer random;

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return RangeRuleInt with set Randomizer.
     */
    public RangeRuleInt withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
     *
     * @param rangeMarkers array of ints that denote the ranges.
     * @return RangeRuleInt with set ranges.
     */
    public static RangeRuleInt withRanges(Integer... rangeMarkers) {
        RangeRuleInt result = new RangeRuleInt();

        result.ranges.addAll(Arrays.asList(rangeMarkers));
        result.rangeEdges.addAll(Arrays.asList(rangeMarkers));

        return result;
    }

    @Override
    public boolean isExclusive() {
        // TODO considering removing exclusive rules altogether
        return false;
    }

    @Override
    public Rule<Integer> recalculatePrecedence(Rule<?> exclusiveRule) {
        // TODO considering removing exclusive rules altogether
        return null;
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

        int randomValue = (int) random.nextLong(ranges.get(randomRangeIndex * 2), ranges.get((randomRangeIndex * 2) + 1));

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

}
