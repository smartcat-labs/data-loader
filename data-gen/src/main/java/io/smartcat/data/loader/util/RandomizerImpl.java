package io.smartcat.data.loader.util;

import java.util.Random;

/**
 * Wrapper for java.util.Random.
 */
public class RandomizerImpl implements Randomizer {

    private Random random = new Random();

    /**
     * Returns a random int value between 0 (inclusive) and the specified value (exclusive).
     * @param bound
     * @return random int
     */
    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    /**
     * Returns a random long value between 0 (inclusive) and the specified value (exclusive).
     */
    @Override
    public long nextLong(long bound) {
        return random.longs(1, 0, bound).findFirst().getAsLong();
    }

    /**
     * Returns a random long value between specified lower value (inclusive) and specified upper value (exclusive).
     */
    @Override
    public long nextLong(long lower, long upper) {
        return random.longs(1, lower, upper).findFirst().getAsLong();
    }

    @Override
    public double nextDouble(double lower, double upper) {
        return random.doubles(1, lower, upper).findFirst().getAsDouble();
    }

    @Override
    public boolean nextBoolean() {
        return random.nextBoolean();
    }



}
