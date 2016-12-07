package io.smartcat.data.loader.rules;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T>
 */
public interface Rule<T> {

    /**
     * Defines rule exclusivity.
     *
     * @return is rule exclusive
     */
    boolean isExclusive();

    /**
     * Recalculate rule precedence. Exclusive rules have precedence over non-exclusive ones. This means that values for
     * the attribute defined by exclusive rule cannot be used in non-exclusive rule.
     *
     * For example: if non-exclusive rule defines { "a", "b", "c" } as allowed values (for certain attribute) and
     * exclusive rule defines values { "c", "d", "e" }, non-exclusive rule allowed values will be recalculated and will
     * only include those values that are not found in the exclusive one. In this case non-exclusive rule will have only
     * { "a", "b" } values, since "c" is found in the exclusive rule.
     *
     * @param exclusiveRule exclusive rule
     * @return Resulting rule
     */
    Rule<T> recalculatePrecedance(Rule<?> exclusiveRule);

    /**
     * Get random allowed value.
     *
     * @return random allowed value
     */
    T getRandomAllowedValue();

}
