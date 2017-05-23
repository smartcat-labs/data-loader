package io.smartcat.ranger.distribution;

import java.util.Random;

/**
 * Uniform distribution.
 */
public class UniformDistribution implements Distribution {

    private Random random = new Random();

    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    @Override
    public int nextInt(int lower, int upper) {
        return random.ints(1, lower, upper).findFirst().getAsInt();
    }

    @Override
    public long nextLong(long bound) {
        return random.longs(1, 0, bound).findFirst().getAsLong();
    }

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
