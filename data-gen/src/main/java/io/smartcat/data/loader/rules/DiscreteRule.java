package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for discrete set of allowed values (i.e. not range).
 */
public class DiscreteRule implements Rule<String> {

    private boolean exclusive;

    private final List<String> allowedValues = new ArrayList<>();

    private Randomizer random;

    private DiscreteRule() {
    };

    /**
     * Set list of allowed String values for the rule.
     * 
     * @param allowedValues array of allowed values
     * @return DiscreteRule with allowed values.
     */
    public static DiscreteRule newSet(String... allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(Arrays.asList(allowedValues));

        return result;
    }

    /**
     * Set Randomizer for the Rule.
     * 
     * @param random Randomizer impl.
     * @return DiscreteRule with set Randomizer.
     */
    public DiscreteRule withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    /**
     * Set list of allowed String values for the rule.
     * 
     * @param allowedValues list of allowed values
     * @return DiscreteRule with list of allowed values.
     */
    public static DiscreteRule newSet(List<String> allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(allowedValues);

        return result;
    }

    /**
     * Set exclusive list of values for the rule, meaning that only builder that uses this instance of the rule can
     * create value for the property with any of these values.
     *
     * @param allowedValues array of Strings that denote the exclusive values.
     * @return exclusive exclusive DiscreteRule with allowed values.
     */
    public static DiscreteRule newSetExclusive(String... allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.exclusive = true;
        result.allowedValues.addAll(Arrays.asList(allowedValues));

        return result;
    }

    @Override
    public boolean isExclusive() {
        return exclusive;
    }

    public List<String> getAllowedValues() {
        return this.allowedValues;
    }

    @Override
    public Rule<String> recalculatePrecedance(Rule<?> exclusiveRule) {
        if (!exclusiveRule.isExclusive()) {
            throw new IllegalArgumentException("no need to calculate rule precedance with non exclusive rule");
        }
        if (!(exclusiveRule instanceof DiscreteRule)) {
            throw new IllegalArgumentException("cannot compare discrete and range rules");
        }
        DiscreteRule otherRule = (DiscreteRule) exclusiveRule;

        allowedValues.removeAll(otherRule.getAllowedValues());

        return DiscreteRule.newSet(allowedValues).withRandom(random);
    }

    @Override
    public String getRandomAllowedValue() {
        int randomIndex = this.random.nextInt(allowedValues.size());
        String value = allowedValues.get(randomIndex);
        return value;
    }

}
