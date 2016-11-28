package io.smartcat.data.loader.rules;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T>
 */
public interface Rule<T> {

    boolean isExclusive();

    Rule<?> recalculatePrecedance(Rule<?> exclusiveRule);

    T getRandomAllowedValue();

}
