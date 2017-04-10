package io.smartcat.data.loader.rategenerator;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.RateGenerator;

/**
 * Abstract class for implementing periodic rate generators. Handles period extraction and value normalization.
 */
public abstract class PeriodicRateGenerator implements RateGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicRateGenerator.class);

    private final long periodInNanos;

    /**
     * Constructs rate generator with specified <code>periodInSeconds</code>.
     *
     * @param periodInSeconds Period of periodic function. Represented in seconds, must be positive number.
     */
    public PeriodicRateGenerator(long periodInSeconds) {
        if (periodInSeconds <= 0) {
            throw new IllegalArgumentException("Period must be positive number.");
        }
        this.periodInNanos = TimeUnit.SECONDS.toNanos(periodInSeconds);
    }

    @Override
    public long getRate(long time) {
        double valueInPeriod = normalizeValue(time);
        long result = rateFunction(valueInPeriod);
        LOGGER.trace("rateFunction returned: {} for value: {}", result, valueInPeriod);
        return result < 0 ? 0 : result;
    }

    /**
     * Method implementing rate function for single period.
     *
     * @param value Value on x axis of periodic function representing single period. Value is guaranteed to be in range
     *            [0,1).
     * @return Rate for this {@link PeriodicRateGenerator} implementation.
     */
    protected abstract long rateFunction(double value);

    private double normalizeValue(long time) {
        long rangeValue = time % periodInNanos;
        return 1d * rangeValue / periodInNanos;
    }
}
