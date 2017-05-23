package io.smartcat.ranger.rules;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Rule for discrete set of allowed values (i.e. not range).
 */
public class DiscreteRuleBoolean implements Rule<Boolean> {

    private Distribution distribution;

    /**
     * Constructs boolean discrete rule with default distribution.
     */
    public DiscreteRuleBoolean() {
        this.distribution = DEFAULT_DISTRIBUTION;
    }

    /**
     * Constructs boolean dictrete rule with specified <code>distribution</code>.
     *
     * @param distribution Distribution to be used when generating values.
     */
    public DiscreteRuleBoolean(Distribution distribution) {
        this.distribution = distribution;
    }

    @Override
    public Boolean next() {
        return distribution.nextBoolean();
    }

}
