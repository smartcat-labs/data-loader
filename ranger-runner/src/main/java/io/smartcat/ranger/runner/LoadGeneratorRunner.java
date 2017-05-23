package io.smartcat.ranger.runner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.reflections.Reflections;

import io.smartcat.ranger.configuration.BaseConfiguration;
import io.smartcat.ranger.configuration.ConfigurationParseException;
import io.smartcat.ranger.configuration.DataSourceConfiguration;
import io.smartcat.ranger.configuration.GlobalConfiguration;
import io.smartcat.ranger.configuration.LoadGeneratorConfiguration;
import io.smartcat.ranger.configuration.RateGeneratorConfiguration;
import io.smartcat.ranger.configuration.WorkerConfiguration;
import io.smartcat.ranger.configuration.YamlConfigurationLoader;
import io.smartcat.ranger.load.generator.LoadGenerator;
import io.smartcat.ranger.load.generator.api.DataSource;
import io.smartcat.ranger.load.generator.api.RateGenerator;
import io.smartcat.ranger.load.generator.api.Worker;
import io.smartcat.ranger.load.generator.worker.AsyncWorker;

/**
 * Runner which takes configuration file, constructs {@link LoadGenerator} with depending {@link DataSource},
 * {@link RateGenerator} and {@link Worker} and runs load generator.
 */
public class LoadGeneratorRunner {

    private static final String RANGER_BASE_PACKAGE = "io.smartcat.ranger";
    private static final String CONFIG_SHORT = "c";
    private static final String CONFIG_LONG = "config";

    private LoadGeneratorRunner() {
    }

    /**
     * Main method for starting load generator runner.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("java -jar ranger-runner.jar -c <path_to_config_file>", options);
        }
        if (cmd == null) {
            return;
        }
        try {
            generateLoad(cmd.getOptionValue(CONFIG_LONG));
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static void generateLoad(String configFilePath) throws Exception {
        YamlConfigurationLoader configurationLoader = new YamlConfigurationLoader();
        GlobalConfiguration configuration = configurationLoader.loadConfig(getURL(configFilePath));
        LoadGeneratorConfiguration loadGeneratorConfiguration = configuration.loadGeneratorConfiguration;
        loadGeneratorConfiguration.validate();
        DataSource dataSource = getDataSource(loadGeneratorConfiguration.dataSourceConfigurationName,
                configuration.dataSourceConfiguration);
        RateGenerator rateGenerator = getRateGenerator(loadGeneratorConfiguration.rateGeneratorConfigurationName,
                configuration.rateGeneratorConfiguration);
        Worker workerDelegate = getWorker(loadGeneratorConfiguration.workerConfigurationName,
                configuration.workerConfiguration);
        Worker worker = wrapIntoAsyncWorker(workerDelegate, loadGeneratorConfiguration.threadCount,
                loadGeneratorConfiguration.queueCapacity);

        LoadGenerator loadGenerator = new LoadGenerator(dataSource, rateGenerator, worker);
        loadGenerator.run();
    }

    private static Options getOptions() {
        Option configOption = new Option(CONFIG_SHORT, CONFIG_LONG, true,
                "Path to config YAML file containing runner configuration.");
        configOption.setRequired(true);

        Options options = new Options();
        options.addOption(configOption);
        return options;
    }

    private static URL getURL(String path) throws IOException {
        return new File(path).getCanonicalFile().toURI().toURL();
    }

    private static DataSource<?> getDataSource(String name, Map<String, Object> configuration)
            throws InstantiationException, IllegalAccessException, ConfigurationParseException {
        DataSourceConfiguration dataSourceConfguration = getConfigurationWithName(name, DataSourceConfiguration.class);
        return dataSourceConfguration.getDataSource(configuration);
    }

    private static RateGenerator getRateGenerator(String name, Map<String, Object> configuration)
            throws ConfigurationParseException {
        RateGeneratorConfiguration rateGeneratorConfguration = getConfigurationWithName(name,
                RateGeneratorConfiguration.class);
        return rateGeneratorConfguration.getRateGenerator(configuration);
    }

    private static Worker<?> getWorker(String name, Map<String, Object> configuration)
            throws ConfigurationParseException {
        WorkerConfiguration workerConfguration = getConfigurationWithName(name, WorkerConfiguration.class);
        return workerConfguration.getWorker(configuration);
    }

    private static <T> Worker<T> wrapIntoAsyncWorker(Worker<T> workerDelegate, int threadCount, int queueCapacity) {
        return new AsyncWorker<>(workerDelegate, queueCapacity, (x) -> {
        }, true, threadCount);
    }

    private static <T extends BaseConfiguration> T getConfigurationWithName(String name, Class<T> clazz) {
        try {
            List<T> configurations = new ArrayList<T>();
            List<String> classNames = new ArrayList<String>();
            for (Class<? extends T> configurationClass : getSubTypesOf(clazz)) {
                T configuration = configurationClass.newInstance();
                if (name.equals(configuration.getName())) {
                    configurations.add(configuration);
                    classNames.add(configurationClass.getCanonicalName());
                }
            }
            if (configurations.isEmpty()) {
                throw new RuntimeException("Configuration with name: " + name + " not found.");
            }
            if (configurations.size() > 1) {
                throw new RuntimeException("Found " + configurations.size() + " configurations on classpath for name: "
                        + name + ", but expected 1. Configuration classes found: " + classNames.toString());
            }
            return configurations.get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Set<Class<? extends T>> getSubTypesOf(Class<T> clazz) {
        Reflections reflections = new Reflections(RANGER_BASE_PACKAGE);
        return reflections.getSubTypesOf(clazz);
    }
}
