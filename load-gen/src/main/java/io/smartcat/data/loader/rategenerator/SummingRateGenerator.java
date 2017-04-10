package io.smartcat.data.loader.rategenerator;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.data.loader.api.RateGenerator;

/**
 * Rate generator that sums up rates of all the underlying rate generators for particular point of time.
 */
public class SummingRateGenerator implements RateGenerator {

    private final List<RateGenerator> rateGenerators;

    /**
     * Constructs rate generator with specified list of <code>rateGenerators</code>.
     *
     * @param rateGenerators List of rate generators to be used, cannot be null nor empty.
     */
    public SummingRateGenerator(List<RateGenerator> rateGenerators) {
        if (rateGenerators == null || rateGenerators.isEmpty()) {
            throw new IllegalArgumentException("Rate generators cannot be null nor empty");
        }
        this.rateGenerators = new ArrayList<>(rateGenerators);
    }

    @Override
    public long getRate(long time) {
        long result = 0;
        for (RateGenerator rateGenerator : rateGenerators) {
            result += rateGenerator.getRate(time);
        }
        return result;
    }
}
