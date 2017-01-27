package io.smartcat.data.loader.rules;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T>
 */
public interface Rule<T> {

    /**
     * Get random allowed value.
     *
     * @return random allowed value
     */
    T getRandomAllowedValue();

}
