package io.smartcat.data.loader.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Rule for discrete set of allowed values (i.e. not range).
 */
public class DiscreteRule implements Rule<String> {

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

    @Override
    public String getRandomAllowedValue() {
        int randomIndex = this.random.nextInt(allowedValues.size());
        String value = allowedValues.get(randomIndex);
        return value;
    }

}
