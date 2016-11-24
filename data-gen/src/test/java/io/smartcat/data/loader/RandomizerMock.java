package io.smartcat.data.loader;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Mock class to be used in tests.
 */
public class RandomizerMock implements Randomizer {

    private int seedInt = 0;
    private long seedLong = 0;
    private double seedDouble = 0;

    @Override
    public int nextInt(int bound) {
        return seedInt++;
    }

    @Override
    public long nextLong(long bound) {
        return seedLong++;
    }

    @Override
    public long nextLong(long lower, long upper) {
        return seedLong++;
    }

    @Override
    public double nextDouble(double lower, double upper) {
        return seedDouble++;
    }

    @Override
    public boolean nextBoolean() {
        return true;
    }

}
