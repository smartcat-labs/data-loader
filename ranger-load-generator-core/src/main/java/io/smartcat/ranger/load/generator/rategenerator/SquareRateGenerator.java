package io.smartcat.ranger.load.generator.rategenerator;

/**
 * Rate generator which uses square wave to generate rate.
 */
public class SquareRateGenerator extends PeriodicRateGenerator {

    private final double leftSide;
    private final long lowerValue;
    private final long upperValue;

    /**
     * Constructs rate generator with specified <code>periodInSeconds</code>, <code>leftSide</code>,
     * <code>lowerValue</code> and <code>upperValue</code>.
     *
     * @param periodInSeconds Period in seconds, must be positive number.
     * @param leftSide Percentage of the period where function has value of <code>lowerValue</code>, must be in range
     *            [0,1).
     * @param lowerValue Lower value of the square function, must be positive number.
     * @param upperValue Upper value of the square function, must be positive number.
     */
    public SquareRateGenerator(long periodInSeconds, double leftSide, long lowerValue, long upperValue) {
        super(periodInSeconds);
        if (leftSide < 0 || leftSide >= 1) {
            throw new IllegalArgumentException("Left side must be in range [0,1).");
        }
        if (lowerValue <= 0) {
            throw new IllegalArgumentException("Lower value must be positive number.");
        }
        if (upperValue <= 0) {
            throw new IllegalArgumentException("Upper value must be positive number.");
        }
        this.leftSide = leftSide;
        this.lowerValue = lowerValue;
        this.upperValue = upperValue;
    }

    @Override
    protected long rateFunction(double value) {
        return Math.round(value < leftSide ? lowerValue : upperValue);
    }
}
