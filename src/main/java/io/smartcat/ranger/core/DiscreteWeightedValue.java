package io.smartcat.ranger.core;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

/**
 * Randomly selects one of the provided values following the provided weights.
 *
 * @param <T> Type this value would evaluate to.
 */
public class DiscreteWeightedValue<T> extends Value<T> {

    private final EnumeratedDistribution<Value<T>> enumeratedDistribution;
    private final List<Value<T>> values;

    /**
     * Constructs discrete weighted value with specified <code>values</code> and <code>weights</code>.
     *
     * @param weightedValues List of values with their corresponding weights.
     */
    public DiscreteWeightedValue(List<WeightedValue<T>> weightedValues) {
        if (weightedValues == null || weightedValues.isEmpty()) {
            throw new IllegalArgumentException("List of weighted values cannot be null nor empty.");
        }
        this.enumeratedDistribution = new EnumeratedDistribution<>(mapToPairList(weightedValues));
        this.values = weightedValues.stream().map(x -> x.getValue()).collect(Collectors.toList());
    }

    @Override
    public void reset() {
        super.reset();
        values.forEach(v -> v.reset());
    }

    @Override
    protected void eval() {
        val = enumeratedDistribution.sample().get();
    }

    private List<Pair<Value<T>, Double>> mapToPairList(List<WeightedValue<T>> weightedValues) {
        return weightedValues.stream().map(x -> new Pair<Value<T>, Double>(x.getValue(), x.getWeight()))
                .collect(Collectors.toList());
    }

    /**
     * Represents value with its weight.
     *
     * @param <T> Type which value will return.
     */
    public static class WeightedValue<T> {
        private final Value<T> value;
        private final double weight;

        /**
         * Constructs weighted value with specified <code>value</code> and <code>weight</code>.
         *
         * @param value The value.
         * @param weigth Weight of the value.
         */
        public WeightedValue(Value<T> value, double weigth) {
            if (value == null) {
                throw new IllegalArgumentException("Value cannot be null.");
            }
            if (weigth <= 0) {
                throw new IllegalArgumentException("Weight must be greater than 0.");
            }
            this.value = value;
            this.weight = weigth;
        }

        /**
         * Returns the value.
         *
         * @return The value.
         */
        public Value<T> getValue() {
            return value;
        }

        /**
         * Returns weight of the value.
         *
         * @return weight of the value.
         */
        public double getWeight() {
            return weight;
        }
    }
}
