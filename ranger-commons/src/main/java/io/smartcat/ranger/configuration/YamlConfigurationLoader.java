package io.smartcat.ranger.configuration;

import static com.google.common.base.CaseFormat.*;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;

/**
 * YAML implementation of {@link ConfigurationLoader}.
 */
public class YamlConfigurationLoader implements ConfigurationLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(YamlConfigurationLoader.class);
    private static final String HYPHEN = "-";
    private static final String CONFIGURATION_PROPERTY_NAME = "ranger.config";

    /**
     * Default external configuration file name.
     */
    private static final String DEFAULT_CONFIGURATION_URL = "ranger-default.yml";

    /**
     * Determines and returns the external configuration URL.
     *
     * @return {@link URL} configuration file URL
     * @throws ConfigurationException in case of a bogus URL
     */
    private URL getStorageConfigUrl() throws ConfigurationException {
        String configUrl = System.getProperty(CONFIGURATION_PROPERTY_NAME);
        if (configUrl == null) {
            configUrl = DEFAULT_CONFIGURATION_URL;
            LOGGER.info("Using default configuration " + DEFAULT_CONFIGURATION_URL);
        }

        URL url;
        try {
            url = new URL(configUrl);
            url.openStream().close(); // catches well-formed but bogus URLs
        } catch (Exception err) {
            ClassLoader loader = YamlConfigurationLoader.class.getClassLoader();
            url = loader.getResource(configUrl);
            if (url == null) {
                String required = "file:" + File.separator + File.separator;
                if (!configUrl.startsWith(required)) {
                    throw new ConfigurationException("Expecting URI in variable [" + CONFIGURATION_PROPERTY_NAME + "]. "
                            + "Please prefix the file with " + required + File.separator + " for local files or "
                            + required + "<server>" + File.separator + " for remote files. Aborting.");
                }
                throw new ConfigurationException(
                        "Cannot locate " + configUrl + ".  If this is a local file, please confirm you've provided "
                                + required + File.separator + " as a URI prefix.");
            }
        }
        return url;
    }

    @Override
    public GlobalConfiguration loadConfig() throws ConfigurationException {
        return loadConfig(getStorageConfigUrl());
    }

    @Override
    public GlobalConfiguration loadConfig(URL url) throws ConfigurationException {
        try {
            LOGGER.info("Loading settings from {}", url);
            Constructor constructor = new Constructor(GlobalConfiguration.class);

            // treat dashed properties as camel case properties
            constructor.setPropertyUtils(new PropertyUtils() {
                @Override
                public Property getProperty(Class<? extends Object> type, String name) throws IntrospectionException {
                    if (name.contains(HYPHEN)) {
                        name = LOWER_HYPHEN.to(LOWER_CAMEL, name);
                    }
                    return super.getProperty(type, name);
                }
            });

            Yaml yaml = new Yaml(constructor);
            GlobalConfiguration result;
            try (InputStream is = url.openStream()) {
                result = yaml.loadAs(is, GlobalConfiguration.class);
            } catch (IOException e) {
                throw new AssertionError(e);
            }

            if (result == null) {
                throw new ConfigurationException("Invalid yaml");
            }
            return result;
        } catch (YAMLException e) {
            throw new ConfigurationException("Invalid yaml", e);
        }
    }
}
