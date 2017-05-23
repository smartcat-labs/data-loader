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

    /**
     * Number of threads to be used by Load Generator.
     */
    public int threadCount;

    /**
     * Capacity of queue to be used by Load Generator.
     */
    public int queueCapacity;

    /**
     * Validates this configuration.
     *
     * @throws ConfigurationException If configuration is not correct.
     */
    public void validate() throws ConfigurationException {
        validateProperty(dataSourceConfigurationName, "data-source-configuration-name");
        validateProperty(rateGeneratorConfigurationName, "rate-generator-configuration-name");
        validateProperty(workerConfigurationName, "worker-configuration-name");
        validateProperty(threadCount, "thread-count");
        validateProperty(queueCapacity, "queue-capacity");
    }

    private void validateProperty(int property, String propertyName) throws ConfigurationException {
        if (property <= 0) {
            throw new ConfigurationException(
                    "Property: '" + propertyName + "' is mandatory. It needs to be positive number.");
        }
    }
    private void validateProperty(String property, String propertyName) throws ConfigurationException {
        if (property == null || property.isEmpty()) {
            throw new ConfigurationException(
                    "Property: '" + propertyName + "' is mandatory. It cannot be null nor empty.");
        }
    }
}
