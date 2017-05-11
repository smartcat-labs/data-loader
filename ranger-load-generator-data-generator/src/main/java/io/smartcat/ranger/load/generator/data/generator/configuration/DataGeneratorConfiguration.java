package io.smartcat.ranger.load.generator.data.generator.configuration;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smartcat.ranger.configuration.DataSourceConfiguration;
import io.smartcat.ranger.configuration.model.KafkaPayload;
import io.smartcat.ranger.data.generator.AggregatedObjectGenerator;
import io.smartcat.ranger.data.generator.ObjectGenerator;
import io.smartcat.ranger.load.generator.api.DataSource;
import io.smartcat.ranger.load.generator.data.generator.datasource.DataGeneratorDataSource;

/**
 * Configuration to construct {@link DataGeneratorDataSource}.
 */
public class DataGeneratorConfiguration implements DataSourceConfiguration {

    @Override
    public String getName() {
        return "DataGenerator";
    }

    @Override
    public DataSource<?> getDataSource(Map<String, Object> configuration) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectConfiguration objectConfiguration = objectMapper.convertValue(configuration.get("objectConfiguration"),
                ObjectConfiguration.class);
        AggregatedObjectGenerator<KafkaPayload> aggregatedObjectGenerator = createAggregatedObjectGenerator(
                objectConfiguration);
        return new DataGeneratorDataSource(aggregatedObjectGenerator);
    }

    private AggregatedObjectGenerator<KafkaPayload> createAggregatedObjectGenerator(
            ObjectConfiguration objectConfiguration) {
        ObjectGenerator.Builder<KafkaPayload> objectGeneratorBuilder = new ObjectGenerator.Builder<KafkaPayload>(
                KafkaPayload.class);
        objectGeneratorBuilder.toBeGenerated(objectConfiguration.getNumberOfObjects());
        for (Field field : objectConfiguration.getFields()) {
            String name = field.getName();
            String[] values = field.getValues().split(",");
            objectGeneratorBuilder.randomFrom(name, values);
        }
        ObjectGenerator<KafkaPayload> objectGenerator = objectGeneratorBuilder.build();
        AggregatedObjectGenerator<KafkaPayload> aggregatedObjectGenerator =
                new AggregatedObjectGenerator.Builder<KafkaPayload>().withObjectGenerator(objectGenerator).build();
        return aggregatedObjectGenerator;
    }
}
