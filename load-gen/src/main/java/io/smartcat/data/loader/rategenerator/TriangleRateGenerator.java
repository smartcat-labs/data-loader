package io.smartcat.data.loader.rategenerator;

/**
 * Rate generator which uses triangle wave to generate rate.
 */
public class TriangleRateGenerator extends PeriodicRateGenerator {

    private final double leftSide;
    private final double minValue;
    private final double maxValue;

    /**
     * Constructs rate generator with specified <code>periodInSeconds</code>, <code>leftSide</code>,
     * <code>lowerValue</code> and <code>upperValue</code>.
     *
     * @param periodInSeconds Period in seconds, must be positive number.
     * @param leftSide Percentage of the period where function is in ascending slope from <code>minValue</code> to
     *            <code>maxValue</code>. Other part of period is in descending slope from <code>maxValue</code> to
     *            <code>minValue</code>, must be in range [0,1).
     * @param minValue Minimum value of the triangle function, must be positive number.
     * @param maxValue Maximum value of the triangle function, must be positive number.
     */
    public TriangleRateGenerator(long periodInSeconds, double leftSide, double minValue, double maxValue) {
        super(periodInSeconds);
        if (leftSide < 0 || leftSide >= 1) {
            throw new IllegalArgumentException("Left side must be in range [0, 1).");
        }
        if (minValue <= 0) {
            throw new IllegalArgumentException("Min value must be positive number.");
        }
        if (maxValue <= 0) {
            throw new IllegalArgumentException("Max value must be positive number.");
        }
        this.leftSide = leftSide;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    protected long rateFunction(double value) {
        if (value < leftSide) {
            return Math.round((maxValue * value / leftSide) + minValue);
        } else {
            return Math.round((maxValue * (1 - value) / (1 - leftSide)) + minValue);
        }
    }
}
