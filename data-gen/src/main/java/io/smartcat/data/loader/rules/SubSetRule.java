package io.smartcat.data.loader.rules;

import java.util.ArrayList;
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

    private final Set<T> values;

    private final Randomizer random;

    private SubSetRule(Builder<T> builder) {
        this.values = builder.values;
        this.random = builder.random;
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

    /**
     * Builder for SubSetRule.
     *
     * @param <T>
     */
    public static class Builder<T> {

        private final Set<T> values = new HashSet<>();
        private Randomizer random;

        /**
         * Constructor.
         *
         * @param randomizer Randomizer implementation.
         */
        public Builder(Randomizer randomizer) {
            this.random = randomizer;
        }

        /**
         * Set of allowed values of type {@code <T>}.
         * @param allowedValues set
         * @return Builder with set values.
         */
        public Builder<T> withValues(Set<T> allowedValues) {
            this.values.addAll(allowedValues);
            return this;
        }

        /**
         * Build method.
         * @return SubSetRule object based on the previously instantiated builder.
         */
        public SubSetRule<T> build() {
            return new SubSetRule<T>(this);
        }

    }

}
