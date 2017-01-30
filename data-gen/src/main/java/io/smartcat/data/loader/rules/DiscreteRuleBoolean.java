package io.smartcat.data.loader.rules;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for discrete set of allowed values (i.e. not range).
 */
public class DiscreteRuleBoolean implements Rule<Boolean> {

    private Randomizer random;

    private DiscreteRuleBoolean(Builder builder) {
        this.random = builder.random;
    }

    @Override
    public Boolean getRandomAllowedValue() {
        return random.nextBoolean();
    }

    /**
     * Builder for DiscreteRuleBoolean.
     */
    public static class Builder {
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
         * Build method.
         *
         * @return immutable DiscreteRuleBoolean object based on the previously instantiated builder.
         */
        public DiscreteRuleBoolean build() {
            return new DiscreteRuleBoolean(this);
        }
    }

}
