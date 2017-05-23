package io.smartcat.ranger.load.generator.configuration;

import java.util.Map;

import io.smartcat.ranger.configuration.ConfigurationParseException;
import io.smartcat.ranger.configuration.DataSourceConfiguration;
import io.smartcat.ranger.load.generator.api.DataSource;
import io.smartcat.ranger.load.generator.datasource.RandomDoubleDataSource;
import io.smartcat.ranger.load.generator.datasource.RandomIntDataSource;
import io.smartcat.ranger.load.generator.datasource.RandomLongDataSource;

/**
 * Configuration to construct one of the following: {@link RandomIntDataSource}, {@link RandomLongDataSource} or
 * {@link RandomDoubleDataSource}.
 *
 * Map needs to contain key '<code>type</code>' and have one of the following values: '<code>int</code>',
 * '<code>long</code>', '<code>double</code>'.
 */
public class RandomNumberDataSourceConfiguration implements DataSourceConfiguration {

    private static final String TYPE = "type";
    private static final String TYPE_INT = "int";
    private static final String TYPE_LONG = "long";
    private static final String TYPE_DOUBLE = "double";

    @Override
    public String getName() {
        return "RandomDataSource";
    }

    @Override
    public DataSource<?> getDataSource(Map<String, Object> configuration) throws ConfigurationParseException {
        if (!configuration.containsKey(TYPE)) {
            throw new ConfigurationParseException("Property '" + TYPE + "' is mandatory.");
        }
        String type = (String) configuration.get(TYPE);
        if (TYPE_INT.equals(type)) {
            return new RandomIntDataSource();
        } else if (TYPE_LONG.equals(type)) {
            return new RandomLongDataSource();
        } else if (TYPE_DOUBLE.equals(type)) {
            return new RandomDoubleDataSource();
        }
        throw new ConfigurationParseException("Value ' + type + ' is not supported for property '" + TYPE + "'.");
    }
}
