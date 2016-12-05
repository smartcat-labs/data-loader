package io.smartcat.data.loader.util;

/**
 * Provides random values.
 */
public interface Randomizer {

    /**
     * Next random int.
     * 
     * @param bound the upper bound exclusive. Must be positive. Lower bound is zero.
     * @return random int from zero to bound.
     */
    int nextInt(int bound);

    /**
     * Next random long.
     * 
     * @param bound upper bound exclusive. Lower bound is zero.
     * @return random long from zero (inclusive) to bound (exclusive);
     */
    long nextLong(long bound);

    /**
     * Next random long between {@code lower} (inclusive) and {@code upper} (exclusive).
     * 
     * @param lower lower bound (inclusive).
     * @param upper upper bound (exclusive).
     * @return random long.
     */
    long nextLong(long lower, long upper);

    /**
     * Next random double between {@code lower} (inclusive) and {@code upper} (exclusive).
     * 
     * @param lower lower bound (inclusive).
     * @param upper upper bound (exclusive).
     * @return random double;
     */
    double nextDouble(double lower, double upper);

    /**
     * Random boolean.
     * 
     * @return random boolean.
     */
    boolean nextBoolean();

}
