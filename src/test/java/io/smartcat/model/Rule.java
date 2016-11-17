package io.smartcat.model;

public interface Rule<T> {

    boolean isExclusive();

    Rule<T> recalculatePrecedance(Rule<T> exclusiveRule);

    T getRandomAllowedValue();

}
