package io.smartcat.data.loader;

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

    public static DiscreteRule newSet(String... allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(Arrays.asList(allowedValues));

        return result;
    }

    public DiscreteRule withRandom(Randomizer random) {
        this.random = random;
        return this;
    }

    public static DiscreteRule newSet(List<String> allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(allowedValues);

        return result;
    }

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
    public Rule<String> recalculatePrecedance(Rule<String> exclusiveRule) {
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
