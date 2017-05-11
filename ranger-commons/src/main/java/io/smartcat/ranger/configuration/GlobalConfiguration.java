package io.smartcat.ranger.configuration;

import java.util.Map;

/**
 * Global configuration for Load Generator and its dependencies.
 */
public class GlobalConfiguration {

    /**
     * Configuration specific to {@link io.smartcat.ranger.load.generator.api.DataSource DataSource}.
     */
    public Map<String, Object> dataSourceConfiguration;

    /**
     * Configuration specific to {@link io.smartcat.ranger.load.generator.api.Worker Worker}.
     */
    public Map<String, Object> workerConfiguration;

    /**
     * Configuration specific to {@link io.smartcat.ranger.load.generator.api.RateGenerator RateGenerator}.
     */
    public Map<String, Object> rateGeneratorConfiguration;

    /**
     * Configuration explaining which {@link DataSourceConfiguration}, {@link WorkerConfiguration} and
     * {@link RateGeneratorConfiguration} implementations will be used.
     */
    public LoadGeneratorConfiguration loadGeneratorConfiguration;
}
