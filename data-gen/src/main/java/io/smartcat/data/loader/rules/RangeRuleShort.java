package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

public class RangeRuleShort implements Rule<Short> {

    // definition of the range: e.g [a,b,c,d] : a < b <= c < d is a set of ranges: {[a,b),[c,d)}
    private final List<Short> ranges = new ArrayList<>();
    private final List<Short> rangeEdges = new LinkedList<>();

    private Randomizer random;

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return RangeRuleLong with set Randomizer.
     */
    public RangeRuleShort withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set range markers (i.e. a,b,c,d -> [a,b),[c,d)) for the rule.
     *
     * @param rangeMarkers array of shorts that denote the ranges.
     * @return RangeRuleShort with set ranges.
     */
    public static RangeRuleShort withRanges(Short... rangeMarkers) {
        RangeRuleShort result = new RangeRuleShort();

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
    public Rule<Short> recalculatePrecedence(Rule<?> exclusiveRule) {
        // TODO considering removing exclusive rules altogether
        return null;
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

        Short randomValue = (short) random.nextLong(ranges.get(randomRangeIndex * 2), ranges.get((randomRangeIndex * 2) + 1));

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

}
