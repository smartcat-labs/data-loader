package io.smartcat.ranger.rules;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for discrete set of allowed String values.
 *
 * @param <T> Type of value which will be generated.
 */
public class DiscreteRule<T> implements Rule<T> {

    private final List<T> values;
    private final Distribution distribution;

    /**
     * Constructs discrete rule with specified possible <code>values</code>.
     *
     * @param values Possible values.
     */
    public DiscreteRule(List<T> values) {
        this(values, DEFAULT_DISTRIBUTION);
    }

    /**
     * Constrcts discrete rule with specified possible <code>values</code> and specified <code>distribution</code>.
     *
     * @param values Possible values.
     * @param distribution Distribution to be used when generating values.
     */
    public DiscreteRule(List<T> values, Distribution distribution) {
        this.values = new ArrayList<>(values);
        this.distribution = distribution;
    }

    @Override
    public T next() {
        int index = distribution.nextInt(values.size());
        return values.get(index);
    }
}
