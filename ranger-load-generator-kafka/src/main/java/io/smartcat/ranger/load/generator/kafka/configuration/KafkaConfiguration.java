package io.smartcat.ranger.load.generator.kafka.configuration;

import java.util.Map;

import io.smartcat.ranger.configuration.WorkerConfiguration;
import io.smartcat.ranger.load.generator.api.Worker;
import io.smartcat.ranger.load.generator.kafka.worker.KafkaWorker;

/**
 * Configuration for Kafka worker.
 */
public class KafkaConfiguration implements WorkerConfiguration {

    @Override
    public String getName() {
        return "Kafka";
    }

    @Override
    public Worker<?> getWorker(Map<String, Object> configuration) {
        return new KafkaWorker(configuration);
    }
}
