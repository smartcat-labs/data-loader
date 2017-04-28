package io.smartcat.ranger.data.generator.rules;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.smartcat.ranger.data.generator.util.Randomizer;

/**
 * Rule for creating a list of random values that is a sublist of passed allowed values. This class preserves the order
 * of the elements, i.e. for a list (a,b,c,d,e), sublists are: (a,b,c), (a,c,d), (b,d), but (b,a) is not sublist.
 *
 * If {@code <T>} is immutable, this class is immutable as well.
 *
 * @param <T>
 */
public class SubListRule<T> implements Rule<List<T>> {

    private final List<T> values;

    private final Randomizer random;

    private SubListRule(Builder<T> builder) {
        this.values = builder.values;
        this.random = builder.random;
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


    /**
     * Builder for SubListRule.
     *
     * @param <T>
     */
    public static class Builder<T> {

        private final List<T> values = new ArrayList<>();
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
         * List of allowed values of type {@code <T>}.
         * @param allowedValues list
         * @return Builder with set values.
         */
        public Builder<T> withValues(List<T> allowedValues) {
            this.values.addAll(allowedValues);
            return this;
        }

        /**
         * Build method.
         * @return SubListRule object based on the previously instantiated builder.
         */
        public SubListRule<T> build() {
            return new SubListRule<T>(this);
        }

    }
}
