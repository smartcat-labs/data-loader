package io.smartcat.ranger.load.generator.kafka.worker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.yaml.snakeyaml.Yaml;

import io.smartcat.ranger.load.generator.api.Worker;

/**
 * Worker that publishes accepted {@link KafkaMessage}s to Kafka cluster. It uses {@link KafkaProducer} internally to
 * publish messages, producer can be configured using YAML config file containing following
 * <a href="https://kafka.apache.org/documentation/#producerconfigs">configuration properties</a>. Additionally, file
 * must contain <code>topic.name</code> property set to value of the topic to which messages will be published.
 */
public class KafkaWorker implements Worker<KafkaMessage>, AutoCloseable {

    private static final String TOPIC_NAME = "topic.name";

    private final File configFile;

    private String topic;
    private Producer<String, String> producer;

    /**
     * Constructs Kafka worker with specified <code>configFile</code> to be used by {@link KafkaProducer}.
     *
     * @param configFile Config file to be used by {@link KafkaProducer}.
     * @throws FileNotFoundException if the file does not exist, is a directory rather than a regular file, or for some
     *             other reason cannot be opened for reading.
     */
    public KafkaWorker(File configFile) throws FileNotFoundException {
        this.configFile = configFile;
        init();
    }

    @Override
    public void accept(KafkaMessage message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message.getKey(), message.getValue());
        producer.send(record);
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }

    @SuppressWarnings("unchecked")
    private void init() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> configuration = (Map<String, Object>) yaml.load(new FileReader(configFile));
        topic = (String) getProperty(TOPIC_NAME, configuration);
        StringSerializer keySerializer = getSerializer(configuration, true);
        StringSerializer valueSerializer = getSerializer(configuration, false);
        producer = new KafkaProducer<>(configuration, keySerializer, valueSerializer);
    }

    private Object getProperty(String key, Map<String, Object> configuration) {
        if (!configuration.containsKey(key)) {
            throw new RuntimeException(
                    "Property: " + key + " not found in configuration at path: " + configFile.getAbsolutePath());
        }
        return configuration.get(key);
    }

    private StringSerializer getSerializer(Map<String, Object> configuration, boolean isKey) {
        StringSerializer serializer = new StringSerializer();
        serializer.configure(configuration, isKey);
        return serializer;
    }
}
