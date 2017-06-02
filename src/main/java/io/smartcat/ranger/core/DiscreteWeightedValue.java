package io.smartcat.ranger.core;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

/**
 * Randomly selects one of the provided values following the provided weights.
 *
 * @param <T> Type this value would evaluate to.
 */
public class DiscreteWeightedValue<T> implements Value<T> {

    private final Distribution distribution = new UniformDistribution();
    private final List<Value<T>> values;
    private final List<Integer> weights;
    private final int weightSum;

    /**
     * Constructs discrete weighted value with specified <code>values</code> and <code>weights</code>.
     *
     * @param values List of possible values.
     * @param weights List of weights for each value.
     */
    public DiscreteWeightedValue(List<Value<T>> values, List<Integer> weights) {
        this.values = new ArrayList<>(values);
        this.weights = weights;
        this.weightSum = weights.stream().mapToInt(x -> x).sum();
    }

    @Override
    public T eval() {
        int num = distribution.nextInt(weightSum);
        int sum = 0;
        for (int i = 0; i < weights.size(); i++) {
            sum += weights.get(i);
            if (num < sum) {
                return values.get(i).eval();
            }
        }
        throw new RuntimeException("This should never happen.");
    }
}
