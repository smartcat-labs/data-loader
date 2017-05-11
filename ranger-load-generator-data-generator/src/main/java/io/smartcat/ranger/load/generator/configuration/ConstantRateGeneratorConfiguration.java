package io.smartcat.ranger.load.generator.configuration;

import java.util.Map;

import io.smartcat.ranger.configuration.RateGeneratorConfiguration;
import io.smartcat.ranger.load.generator.api.RateGenerator;
import io.smartcat.ranger.load.generator.rategenerator.ConstantRateGenerator;

/**
 * Configuration to construct {@link ConstantRateGenerator}.
 */
public class ConstantRateGeneratorConfiguration implements RateGeneratorConfiguration {

    @Override
    public String getName() {
        return "ConstantRateGenerator";
    }

    @Override
    public RateGenerator getRateGenerator(Map<String, Object> configuration) {
        long rate = (int) configuration.get("rate");
        return new ConstantRateGenerator(rate);
    }
}
