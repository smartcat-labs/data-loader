package io.smartcat.ranger.configuration;

import java.util.Map;

import io.smartcat.ranger.load.generator.api.DataSource;

/**
 * Returns {@link DataSource} based on configuration parameters. Each {@link DataSource} implementation should go with
 * corresponding data source configuration implementation which would be used to construct that data source.
 */
public interface DataSourceConfiguration extends BaseConfiguration {

    /**
     * Returns data source based on configuration parameters.
     *
     * @param configuration Configuration specific to data source it should construct.
     * @return Instance of {@link DataSource}, never null.
     *
     * @throws ConfigurationParseException If there is an error during configuration parsing.
     */
    DataSource<?> getDataSource(Map<String, Object> configuration) throws ConfigurationParseException;
}
