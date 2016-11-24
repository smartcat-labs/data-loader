package io.smartcat.data.loader.util;

/**
 * Provides random values.
 */
public interface Randomizer {

    int nextInt(int bound);

    long nextLong(long bound);

    long nextLong(long lower, long upper);

    double nextDouble(double lower, double upper);

    boolean nextBoolean();

}
