package io.smartcat.ranger.load.generator.rategenerator;

/**
 * Rate generator which uses sine function to generate rate.
 */
public class SineRateGenerator extends PeriodicRateGenerator {

    private final long multiplier;
    private final long independentConstant;

    /**
     * Constructs rate generator with specified <code>periodInSeconds</code>, <code>multiplier</code> and
     * <code>independentConstant</code>.
     *
     * @param periodInSeconds Period in seconds, must be positive number.
     * @param multiplier Multiplies result of sine function.
     * @param independentConstant Adds to the result of multiplied sine function, must be positive number.
     */
    public SineRateGenerator(long periodInSeconds, long multiplier, long independentConstant) {
        super(periodInSeconds);
        if (independentConstant <= 0) {
            throw new IllegalArgumentException("Independent constant must be positive number.");
        }
        this.multiplier = multiplier;
        this.independentConstant = independentConstant;
    }

    @Override
    protected long rateFunction(double value) {
        return Math.round(Math.sin(value * 2 * Math.PI) * multiplier) + independentConstant;
    }
}
