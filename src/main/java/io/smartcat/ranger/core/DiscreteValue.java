package io.smartcat.ranger.core;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

/**
 * Randomly selects one of the provided values following the specified distribution.
 *
 * @param <T> Type this value would evaluate to.
 */
public class DiscreteValue<T> implements Value<T> {

    private final List<Value<T>> values;
    private final Distribution distribution;

    /**
     * Constructs discrete value with specified <code>values</code>, <code>distribution</code> is set to Uniform
     * distribution.
     *
     * @param values List of possible values.
     */
    public DiscreteValue(List<Value<T>> values) {
        this(values, new UniformDistribution());
    }

    /**
     * Constructs discrete value with specified <code>values</code> and <code>distribution</code>.
     *
     * @param values List of possible values.
     * @param distribution Distribution to use for value selection.
     */
    public DiscreteValue(List<Value<T>> values, Distribution distribution) {
        this.values = new ArrayList<>(values);
        this.distribution = distribution;
    }

    @Override
    public T eval() {
        int index = distribution.nextInt(values.size());
        return values.get(index).eval();
    }
}
