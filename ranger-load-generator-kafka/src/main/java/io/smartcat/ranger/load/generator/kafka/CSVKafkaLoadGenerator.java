package io.smartcat.ranger.load.generator.kafka;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import io.smartcat.ranger.load.generator.LoadGenerator;
import io.smartcat.ranger.load.generator.api.RateGenerator;
import io.smartcat.ranger.load.generator.csv.datasource.CSVDataSource;
import io.smartcat.ranger.load.generator.csv.datasource.CSVDataSource.RowMapper;
import io.smartcat.ranger.load.generator.kafka.worker.KafkaMessage;
import io.smartcat.ranger.load.generator.kafka.worker.KafkaWorker;
import io.smartcat.ranger.load.generator.rategenerator.ConstantRateGenerator;
import io.smartcat.ranger.load.generator.worker.AsyncWorker;

/**
 * Kafka console load generator.
 */
public class CSVKafkaLoadGenerator {

    private static final String RATE = "rate";
    private static final String CONFIG = "config";
    private static final String SOURCE = "source";

    private static final String LF = "\n";
    private static final int BUFFER_COEFFICIENT = 5;

    private CSVKafkaLoadGenerator() {
    }

    /**
     * Main method.
     * @param args command line arguments.
     * @throws Exception if error occurs while generating load.
     */
    public static void main(String[] args) throws Exception {
        Options options = getOptions();
        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(
                    "java -jar load-gen-kafka.jar -r <rate> -c <path_to_config_file> -s <path_to_source_file>",
                    options);
        }
        if (cmd == null) {
            return;
        }
        generateLoad(Long.parseLong(cmd.getOptionValue(RATE)), cmd.getOptionValue(CONFIG), cmd.getOptionValue(SOURCE));
    }

    private static void generateLoad(long rate, String configFilePath, String sourceFilePath) throws Exception {
        FileReader fileReader = new FileReader(resolvePath(sourceFilePath));
        CSVFormat format = CSVFormat.DEFAULT.withQuote(null).withRecordSeparator(LF).withTrim();
        try (CSVParser csvParser = new CSVParser(fileReader, format);
                CSVDataSource<KafkaMessage> ds = new CSVDataSource<>(csvParser, kafkaRowMapper());
                KafkaWorker kafkaWorker = new KafkaWorker(resolvePath(configFilePath));
                AsyncWorker<KafkaMessage> asyncWorker = new AsyncWorker<KafkaMessage>(kafkaWorker,
                        (int) (BUFFER_COEFFICIENT * rate))) {
            RateGenerator rg = new ConstantRateGenerator(rate);
            LoadGenerator<KafkaMessage> lg = new LoadGenerator<>(ds, rg, asyncWorker);
            lg.run();
            lg.terminate();
        }
    }

    private static Options getOptions() {
        Option rateOption = new Option("r", RATE, true,
                "Rate per second at which Load Generator will send messages to Kafka.");
        rateOption.setRequired(true);
        Option configOption = new Option("c", CONFIG, true,
                "Path to config YAML file containing Kafka configuration. For more information on "
                        + "config file, visit: https://kafka.apache.org/documentation/#producerconfigs "
                        + "Additionally, file must contain 'topic.name' property set to value of the "
                        + "topic to which messages will be published.");
        configOption.setRequired(true);
        Option sourceOption = new Option("s", SOURCE, true,
                "Path to source CSV file from which data will be loaded. CSV file must have 2 columns "
                        + "with no header. First column is message key, second is message value.");
        sourceOption.setRequired(true);

        Options options = new Options();
        options.addOption(rateOption);
        options.addOption(configOption);
        options.addOption(sourceOption);
        return options;
    }

    private static File resolvePath(String path) throws IOException {
        return new File(path).getCanonicalFile();
    }

    private static RowMapper<KafkaMessage> kafkaRowMapper() {
        return new RowMapper<KafkaMessage>() {

            @Override
            public KafkaMessage map(CSVRecord record) {
                return new KafkaMessage(record.get(0), record.get(1));
            }
        };
    }
}
