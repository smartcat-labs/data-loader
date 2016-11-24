package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating a list of random values that is a sublist of passed allowed values.
 * This class preserves the order of the elements, i.e. for a list (a,b,c,d,e), sublists are:
 * (a,b,c), (a,c,d), (b,d), but (b,a) is not sublist.
 *
 * @param <T>
 */
public class SubListRule<T> implements Rule<List<T>> {

    private boolean exclusive;
    private final List<T> values = new ArrayList<>();

    private Randomizer random;

    private SubListRule() {
    }

    public SubListRule<T> withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    public static <T> SubListRule<T> withValues(List<T> allowedValues) {
        SubListRule<T> subListRule = new SubListRule<>();
        subListRule.values.addAll(allowedValues);
        return subListRule;
    }

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
    public Rule<List<T>> recalculatePrecedance(Rule<List<T>> exclusiveRule) {
        // TODO
        throw new UnsupportedOperationException();
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
