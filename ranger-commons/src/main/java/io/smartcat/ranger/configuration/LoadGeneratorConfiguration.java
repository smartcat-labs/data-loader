package io.smartcat.ranger.configuration;

/**
 * Load Generator configuration explaining which configurations for data source, rate generator and worker should be
 * used.
 */
public class LoadGeneratorConfiguration {

    /**
     * Name of {@link io.smartcat.ranger.load.generator.api.DataSource DataSource} configuration.
     */
    public String dataSourceConfigurationName;

    /**
     * Name of {@link io.smartcat.ranger.load.generator.api.RateGenerator RateGenerator} configuration.
     */
    public String rateGeneratorConfigurationName;

    /**
     * Name of {@link io.smartcat.ranger.load.generator.api.Worker Worker} configuration.
     */
    public String workerConfigurationName;
}
