package io.smartcat.data.loader;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T>
 */
public interface Rule<T> {

    boolean isExclusive();

    Rule<T> recalculatePrecedance(Rule<T> exclusiveRule);

    T getRandomAllowedValue();

}
