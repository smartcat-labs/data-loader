package io.smartcat.ranger.configuration;

import java.util.Map;

import io.smartcat.ranger.load.generator.api.RateGenerator;

/**
 * Returns {@link RateGenerator} based on configuration parameters. Each {@link RateGenerator} implementation should go
 * with corresponding rate generator configuration implementation which would be used to construct that rate generator.
 */
public interface RateGeneratorConfiguration extends BaseConfiguration {

    /**
     * Returns rate generator based on configuration parameters.
     *
     * @param configuration Configuration specific to rate generator it should construct.
     * @return Instance of {@link RateGenerator}, never null.
     */
    RateGenerator getRateGenerator(Map<String, Object> configuration);
}
