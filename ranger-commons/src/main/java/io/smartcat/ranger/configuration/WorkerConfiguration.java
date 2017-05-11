package io.smartcat.ranger.configuration;

import java.util.Map;

import io.smartcat.ranger.load.generator.api.Worker;

/**
 * Returns {@link Worker} based on configuration parameters. Each {@link Worker} implementation should go with
 * corresponding worker configuration implementation which would be used to construct that worker.
 */
public interface WorkerConfiguration extends BaseConfiguration {

    /**
     * Returns worker based on configuration parameters.
     *
     * @param configuration Configuration specific to this worker.
     * @return Instance of {@link Worker}, never null.
     */
    Worker<?> getWorker(Map<String, Object> configuration);
}
