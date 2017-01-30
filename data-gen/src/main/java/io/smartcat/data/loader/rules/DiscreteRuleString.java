package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for discrete set of allowed String values.
 */
public class DiscreteRuleString implements Rule<String> {

    private final List<String> allowedValues = new ArrayList<>();

    private Randomizer random;

    private DiscreteRuleString(Builder builder) {
        this.allowedValues.addAll(builder.allowedValues);
        this.random = builder.random;
    }

    @Override
    public String getRandomAllowedValue() {
        int randomIndex = this.random.nextInt(allowedValues.size());
        String value = allowedValues.get(randomIndex);
        return value;
    }

    /**
     * Builder for DiscreteRuleString.
     */
    public static class Builder {

        private Randomizer random;
        private final List<String> allowedValues = new ArrayList<>();

        /**
         * Constructor.
         *
         * @param randomizer Randomizer implementation.
         */
        public Builder(Randomizer randomizer) {
            this.random = randomizer;
        }

        /**
         * Set list of allowed String values for the rule.
         *
         * @param allowedValues array of allowed values
         * @return Builder with set allowedValues
         */
        public Builder allowedValues(String... allowedValues) {
            this.allowedValues.addAll(Arrays.asList(allowedValues));
            return this;
        }

        /**
         * Set list of allowed String values for the rule.
         *
         * @param allowedValues array of allowed values
         * @return Builder with set allowedValues
         */
       public Builder allowedValues(List<String> allowedValues) {
           this.allowedValues.addAll(allowedValues);
           return this;
       }

        /**
         * Build method.
         *
         * @return immutable DiscreteRuleBoolean object based on the previously instantiated builder.
         */
        public DiscreteRuleString build() {
            return new DiscreteRuleString(this);
        }
    }

}
