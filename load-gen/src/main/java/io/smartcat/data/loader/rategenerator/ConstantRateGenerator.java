package io.smartcat.data.loader.rategenerator;

import io.smartcat.data.loader.api.RateGenerator;

/**
 * Rate generator which generates constant rate.
 */
public class ConstantRateGenerator implements RateGenerator {

    private final long perSecondRate;

    /**
     * Constructs rate generator with specified <code>perSecondRate</code>.
     *
     * @param perSecondRate Rate of the rate generator per second, must be positive number.
     */
    public ConstantRateGenerator(long perSecondRate) {
        if (perSecondRate <= 0) {
            throw new IllegalArgumentException("Rate must be positive number.");
        }
        this.perSecondRate = perSecondRate;
    }

    @Override
    public long getRate(long time) {
        return perSecondRate;
    }
}
