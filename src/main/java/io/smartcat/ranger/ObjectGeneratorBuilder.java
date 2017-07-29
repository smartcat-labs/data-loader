package io.smartcat.ranger;

import java.util.LinkedHashMap;
import java.util.Map;

import io.smartcat.ranger.core.CompositeValue;
import io.smartcat.ranger.core.ConstantValue;
import io.smartcat.ranger.core.TypeConverterValue;
import io.smartcat.ranger.core.Value;

/**
 * Builder for {@link ObjectGenerator}.
 */
public class ObjectGeneratorBuilder {

    private final Map<String, Value<?>> propertyValues;

    /**
     * Constructs {@link ObjectGeneratorBuilder}.
     */
    public ObjectGeneratorBuilder() {
        this.propertyValues = new LinkedHashMap<>();
    }

    /**
     * Sets the value to be used for generating values for property.
     *
     * @param property Name of the property.
     * @param value Value to be used for generating values.
     * @param <V> Type of object which value will be generate.
     * @return This builder.
     */
    @SuppressWarnings({ "rawtypes" })
    public <V> ObjectGeneratorBuilder prop(String property, V value) {
        propertyValues.put(property,
                value instanceof ObjectGenerator ? ((ObjectGenerator) value).value : ConstantValue.of(value));
        return this;
    }

    /**
     * Builds {@link ObjectGenerator} based on current builder configuration. Resulting {@link ObjectGenerator} will
     * have {@code Map<String, Object>} as return type.
     *
     * @return Instance of {@link ObjectGenerator}.
     */
    public ObjectGenerator<Map<String, Object>> build() {
        return new ObjectGenerator<>(new CompositeValue(propertyValues));
    }

    /**
     * Builds {@link ObjectGenerator} based on current builder configuration. Resulting {@link ObjectGenerator} will try
     * to convert configured output to specified <code>objectType</code>.
     *
     * @param objectType Type of object to which conversion will be attempted.
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return Instance of {@link ObjectGenerator}.
     */
    public <T> ObjectGenerator<T> build(Class<T> objectType) {
        if (objectType == null) {
            throw new RuntimeException("objectType cannot be null.");
        }
        return new ObjectGenerator<T>(new TypeConverterValue<>(objectType, new CompositeValue(propertyValues)));
    }

}
