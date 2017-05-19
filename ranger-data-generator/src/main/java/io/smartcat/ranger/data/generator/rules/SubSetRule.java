package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.smartcat.ranger.data.generator.distribution.Distribution;

/**
 * Rule for creating a set of random values that is a subset of passed allowed values.
 *
 * @param <T> Type of value which will be generated.
 */
public class SubSetRule<T> implements Rule<Set<T>> {

    private final Set<T> values;
    private final Distribution distribution;

    /**
     * Constructs rule with possible list of values from which sub set will be generated.
     *
     * @param values Possible values.
     */
    public SubSetRule(Set<T> values) {
        this(values, DEFAULT_DISTRIBUTION);
    }

    /**
     * Constructs rule with possible set of values from which sub set will be generated.
     *
     * @param values Possible values.
     * @param distribution Distribution to be used when generating values.
     */
    public SubSetRule(Set<T> values, Distribution distribution) {
        this.values = new HashSet<>(values);
        this.distribution = distribution;
    }

    @Override
    public Set<T> next() {
        return getRandomSubset(values);
    }

    private Set<T> getRandomSubset(Set<T> values) {
        int size = distribution.nextInt(values.size() + 1);
        List<T> list = new ArrayList<>(values);
        Collections.shuffle(list);
        return new HashSet<>(list.subList(0, size));
    }
}
