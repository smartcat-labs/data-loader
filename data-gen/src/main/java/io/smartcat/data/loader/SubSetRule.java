package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Rule for creating a set of random values that is a subset of passed allowed values.
 *
 * @param <T>
 */
public class SubSetRule<T> implements Rule<Set<T>> {

    private boolean exclusive;
    private final Set<T> values = new HashSet<>();

    private SubSetRule() {
    }

    public static <T> SubSetRule<T> withValues(List<T> allowedValues) {
        SubSetRule<T> subSetRule = new SubSetRule<>();
        subSetRule.values.addAll(allowedValues);
        return subSetRule;
    }

    public static <T> SubSetRule<T> withValuesX(List<T> allowedValues) {
        SubSetRule<T> subSetRule = new SubSetRule<>();
        subSetRule.values.addAll(allowedValues);
        subSetRule.exclusive = true;
        return subSetRule;
    }

    @Override
    public boolean isExclusive() {
        return this.exclusive;
    }

    @Override
    public Rule<Set<T>> recalculatePrecedance(Rule<Set<T>> exclusiveRule) {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<T> getRandomAllowedValue() {
        return getRandomSubset(values);
    }

    private Set<T> getRandomSubset(Set<T> values) {
        int randomSize = ThreadLocalRandom.current().nextInt(0, values.size() + 1);

        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list);
        Set<T> randomSubset = new HashSet<>(list.subList(0, randomSize));

        return randomSubset;
    }

}
