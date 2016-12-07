package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating a list of random values that is a sublist of passed allowed values. This class preserves the order
 * of the elements, i.e. for a list (a,b,c,d,e), sublists are: (a,b,c), (a,c,d), (b,d), but (b,a) is not sublist.
 *
 * @param <T>
 */
public class SubListRule<T> implements Rule<List<T>> {

    private boolean exclusive;
    private final List<T> values = new ArrayList<>();

    private Randomizer random;

    private SubListRule() {
    }

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return SubListRule<T> with set Randomizer.
     */
    public SubListRule<T> withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set list of allowed values for the sublist rule from which the sub list of allowed values will be created.
     *
     * @param <T> type param
     * @param allowedValues list
     * @return SubListRule<T>
     */
    public static <T> SubListRule<T> withValues(List<T> allowedValues) {
        SubListRule<T> subListRule = new SubListRule<>();
        subListRule.values.addAll(allowedValues);
        return subListRule;
    }

    /**
     * Set exclusive list of allowed values for the sublist rule from which the sub list of allowed values will be
     * created.
     *
     * @param <T> type param
     * @param allowedValues list of allowed values
     * @return exclusive SubListRule
     */
    public static <T> SubListRule<T> withValuesX(List<T> allowedValues) {
        SubListRule<T> subListRule = new SubListRule<>();
        subListRule.values.addAll(allowedValues);
        subListRule.exclusive = true;
        return subListRule;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    @Override
    public Rule<List<T>> recalculatePrecedance(Rule<?> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
        }
        if (!(exclusiveRule instanceof SubListRule)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }

        @SuppressWarnings("unchecked") // no way to ensure type safety
        SubListRule<T> otherRule = (SubListRule<T>) exclusiveRule;

        List<T> newList = new ArrayList<>();
        newList.addAll(values);

        newList.removeAll(otherRule.values);

        return SubListRule.withValues(newList).withRandom(random);
    }

    @Override
    public List<T> getRandomAllowedValue() {
        return getRandomSubList(values);
    }

    private List<T> getRandomSubList(List<T> values) {
        List<T> result = new LinkedList<>();
        for (T t : values) {
            boolean shouldAdd = random.nextBoolean();
            if (shouldAdd) {
                result.add(t);
            }
        }
        return result;

    }
}
