package io.smartcat.ranger.load.generator.api;

/**
 * Interface providing API for rate generation. Generates rate per second.
 */
public interface RateGenerator {

    /**
     * Returns rate per second as a function of time.
     *
     * @param time Relative time in nanoseconds from starting load generator.
     * @return Rate per second as a function of time.
     */
    long getRate(long time);
}
