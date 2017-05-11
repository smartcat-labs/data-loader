package io.smartcat.ranger.configuration;

import java.net.URL;

/**
 * Loader for {@link GlobalConfiguration}.
 */
public interface ConfigurationLoader {
    /**
     * Loads configuration from an implicit location.
     *
     * @return loaded configuration
     * @throws ConfigurationException in case the configuration cannot be loaded
     */
    GlobalConfiguration loadConfig() throws ConfigurationException;

    /**
     * Loads configuration using an explicit location.
     *
     * @param url configuration location
     * @return loaded configuration
     * @throws ConfigurationException in case the configuration cannot be loaded
     */
    GlobalConfiguration loadConfig(URL url) throws ConfigurationException;
}
