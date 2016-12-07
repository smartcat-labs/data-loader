package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for creating a set of random values that is a subset of passed allowed values.
 *
 * @param <T>
 */
public class SubSetRule<T> implements Rule<Set<T>> {

    private boolean exclusive;
    private final Set<T> values = new HashSet<>();

    private Randomizer random;

    private SubSetRule() {
    }

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return SubSetRule<T> with set Randomizer.
     */
    public SubSetRule<T> withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set set of allowed values for the subset rule from which the sub set of allowed values will be created.
     *
     * @param <T> type param
     * @param allowedValues collection (duplicate values will be removed)
     * @return SubSetRule<T>
     */
    public static <T> SubSetRule<T> withValues(Collection<T> allowedValues) {
        SubSetRule<T> subSetRule = new SubSetRule<>();
        subSetRule.values.addAll(allowedValues);
        return subSetRule;
    }

    /**
     * Set exclusive set of allowed values for the sub set rule from which the sub set of allowed values will be
     * created.
     *
     * @param <T> type param
     * @param allowedValues collection (duplicate values will be removed)
     * @return exclusive SubSetRule
     */
    public static <T> SubSetRule<T> withValuesX(Collection<T> allowedValues) {
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
    public Rule<Set<T>> recalculatePrecedence(Rule<?> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedence with non exclusive rule");
        }
        if (!(exclusiveRule instanceof SubSetRule)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }

        @SuppressWarnings("unchecked") // no way to ensure type safety
        SubSetRule<T> otherRule = (SubSetRule<T>) exclusiveRule;

        Set<T> newSet = new HashSet<>();
        newSet.addAll(values);

        newSet.removeAll(otherRule.values);

        return SubSetRule.withValues(newSet).withRandom(random);

    }

    @Override
    public Set<T> getRandomAllowedValue() {
        return getRandomSubset(values);
    }

    private Set<T> getRandomSubset(Set<T> values) {
        int randomSize = random.nextInt(values.size() + 1);

        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list);
        Set<T> randomSubset = new HashSet<>(list.subList(0, randomSize));

        return randomSubset;
    }

}
