package io.smartcat.ranger.load.generator.data.generator.datasource;

import java.util.Iterator;

import io.smartcat.ranger.configuration.model.KafkaPayload;
import io.smartcat.ranger.data.generator.AggregatedObjectGenerator;
import io.smartcat.ranger.load.generator.api.DataSource;

/**
 * Data source implementation for ranger-data-generator module.
 */
public class DataGeneratorDataSource implements DataSource<KafkaPayload> {

    private final Iterator<KafkaPayload> iterator;

    /**
     * Constructs data generator data source with specified <code>aggregatedObjectGenerator</code>.
     *
     * @param aggregatedObjectGenerator Generator which will be used to generate objects.
     */
    public DataGeneratorDataSource(AggregatedObjectGenerator<KafkaPayload> aggregatedObjectGenerator) {
        this.iterator = aggregatedObjectGenerator.iterator();
    }

    @Override
    public boolean hasNext(long time) {
        return iterator.hasNext();
    }

    @Override
    public KafkaPayload getNext(long time) {
        return iterator.next();
    }
}
