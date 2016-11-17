package io.smartcat.data.loader;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.google.common.collect.Lists;

public class DiscreteRule implements Rule<String> {

    private boolean exclusive;

    private final List<String> allowedValues = Lists.newArrayList();

    private DiscreteRule() {
    };

    public static DiscreteRule newSet(String... allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(Lists.newArrayList(allowedValues));

        return result;
    }

    public static DiscreteRule newSet(List<String> allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.allowedValues.addAll(Lists.newArrayList(allowedValues));

        return result;
    }

    public static DiscreteRule newSetExclusive(String... allowedValues) {
        DiscreteRule result = new DiscreteRule();

        result.exclusive = true;
        result.allowedValues.addAll(Lists.newArrayList(allowedValues));

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

        return DiscreteRule.newSet(allowedValues);
    }

    @Override
    public String getRandomAllowedValue() {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, allowedValues.size());
        String value = allowedValues.get(randomIndex);
        return value;
    }

}
