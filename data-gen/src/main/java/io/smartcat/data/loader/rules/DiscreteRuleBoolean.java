package io.smartcat.data.loader.rules;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for discrete set of allowed values (i.e. not range).
 */
public class DiscreteRuleBoolean implements Rule<Boolean> {

    private Randomizer random;

    private DiscreteRuleBoolean() {
    };

    /**
     * Set Randomizer for the Rule.
     *
     * @param random Randomizer impl.
     * @return DiscreteRule with set Randomizer.
     */
    public static DiscreteRuleBoolean withRandom(Randomizer random) {
        DiscreteRuleBoolean rule = new DiscreteRuleBoolean();
        rule.random = random;
        return rule;
    }

    @Override
    public Boolean getRandomAllowedValue() {
        return random.nextBoolean();
    }

}
