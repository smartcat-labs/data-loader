package io.smartcat.ranger.core;

/**
 * Root of type hierarchy. It can evaluate to a value.
 *
 * @param <T> Type value would evaluate to.
 */
public interface Value<T> {

    /**
     * Returns a value depending on concrete implementation.
     *
     * @return A value depending on concrete implementation.
     */
    T eval();
}
