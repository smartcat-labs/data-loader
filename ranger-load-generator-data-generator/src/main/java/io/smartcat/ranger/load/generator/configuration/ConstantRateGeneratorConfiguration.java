package io.smartcat.ranger.load.generator.configuration;

import java.util.Map;

import io.smartcat.ranger.configuration.ConfigurationParseException;
import io.smartcat.ranger.configuration.RateGeneratorConfiguration;
import io.smartcat.ranger.load.generator.api.RateGenerator;
import io.smartcat.ranger.load.generator.rategenerator.ConstantRateGenerator;

/**
 * Configuration to construct {@link ConstantRateGenerator}.
 *
 * Map needs to contain key '<code>rate</code>' and have positive <code>long</code> value which represents rate.
 */
public class ConstantRateGeneratorConfiguration implements RateGeneratorConfiguration {

    private static final String RATE = "rate";

    @Override
    public String getName() {
        return "ConstantRateGenerator";
    }

    @Override
    public RateGenerator getRateGenerator(Map<String, Object> configuration) throws ConfigurationParseException {
        if (!configuration.containsKey(RATE)) {
            throw new ConfigurationParseException("Property '" + RATE + "' is mandatory.");
        }
        Object rawRate = configuration.get(RATE);
        long rate;
        if (rawRate instanceof Integer) {
            rate = (int) rawRate;
        } else {
            rate = (long) rawRate;
        }
        return new ConstantRateGenerator(rate);
    }
}
