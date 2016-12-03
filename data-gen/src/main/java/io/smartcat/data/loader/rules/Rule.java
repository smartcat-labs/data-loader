package io.smartcat.data.loader.rules;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T>
 */
public interface Rule<T> {

    /**
     * Defines rule exlusivity.
     *
     * @return is rule exclusive
     */
    boolean isExclusive();

    /**
     * Recalculate rule precedence.
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
