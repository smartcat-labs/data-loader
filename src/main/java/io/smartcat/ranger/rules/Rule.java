package io.smartcat.ranger.rules;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

/**
 * Rule is used for generating random values of certain type.
 *
 * @param <T> Type of value which will be generated.
 */
public interface Rule<T> {

    /**
     * Default distribution.
     */
    static Distribution DEFAULT_DISTRIBUTION = new UniformDistribution();

    /**
     * Get next value.
     *
     * @return Next value.
     */
    T next();

}
