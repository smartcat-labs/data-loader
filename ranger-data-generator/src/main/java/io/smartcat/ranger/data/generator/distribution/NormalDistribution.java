package io.smartcat.ranger.data.generator.distribution;

/**
 * Normal Distribution.
 */
public class NormalDistribution implements Distribution {

    private final org.apache.commons.math3.distribution.NormalDistribution delegate;
    private final double lower;
    private final double upper;
    private final double innerRange;

    /**
     * Constructs Normal distribution.
     * <code>mean</code> is set to 0.5, <code>standardDeviation</code> is set to 0.125, <code>lower</code> is
     * set to 0 and <code>upper</code> is set to 1.
     */
    public NormalDistribution() {
        this(0.5, 0.125, 0, 1);
    }

    /**
     * Constructs Normal distribution with specified <code>mean</code>, <code>standardDeviation</code>,
     * <code>lower</code> and <code>upper</code>.
     * @param mean Mean of Normal distribution.
     * @param standardDeviation Standard deviation of Normal distribution.
     * @param lower Lower bound, generated values lower that this value will be set to this value.
     * @param upper Upper bound, generated values greater that ths value will be set to this value.
     */
    public NormalDistribution(double mean, double standardDeviation, double lower, double upper) {
        this.delegate = new org.apache.commons.math3.distribution.NormalDistribution(mean, standardDeviation);
        this.lower = lower;
        this.upper = upper;
        this.innerRange = upper - lower;
    }

    @Override
    public int nextInt(int bound) {
        return (int) normalize(delegate.sample(), 0, bound);
    }

    @Override
    public int nextInt(int lower, int upper) {
        return (int) normalize(delegate.sample(), lower, upper);
    }

    @Override
    public long nextLong(long bound) {
        return (long) normalize(delegate.sample(), 0, bound);
    }

    @Override
    public long nextLong(long lower, long upper) {
        return (int) normalize(delegate.sample(), lower, upper);
    }

    @Override
    public double nextDouble(double lower, double upper) {
        return normalize(delegate.sample(), lower, upper);
    }

    @Override
    public boolean nextBoolean() {
        return ((long) normalize(delegate.sample(), 0, 100)) % 2 == 0;
    }

    private double normalize(double value, double normalizationLowerBound, double normalizationUpperBound) {
        double boundedValue = boundValue(value);
        // normalize boundedValue to new range
        double normalizedRange = normalizationUpperBound - normalizationLowerBound;
        return (((boundedValue - lower) * normalizedRange) / innerRange) + normalizationLowerBound;
    }

    private double boundValue(double value) {
        double boundedValue = value;
        if (value < lower) {
            boundedValue = lower;
        }
        if (value > upper) {
            boundedValue = upper;
        }
        return boundedValue;
    }
}
