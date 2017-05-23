package io.smartcat.ranger.distribution;

/**
 * Provides random values.
 */
public interface Distribution {

    /**
     * Next random int.
     *
     * @param bound the upper bound exclusive. Must be greater than or equal to 1. Lower bound is zero.
     * @return random int from zero to bound.
     */
    int nextInt(int bound);

    /**
     * Next random int between {@code lower} (inclusive) and {@code upper} (exclusive).
     *
     * @param lower lower bound (inclusive).
     * @param upper upper bound (exclusive).
     * @return random int.
     */
    int nextInt(int lower, int upper);

    /**
     * Next random long.
     *
     * @param bound upper bound exclusive. Must be greater than or equal to 1. Lower bound is zero.
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
