package io.smartcat.ranger.core;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Converts value to specified type.
 *
 * @param <T> Type to which to convert value.
 */
public class TypeConverterValue<T> extends Transformer<T> {

    private final Class<T> objectType;
    private final Value<?> value;
    private final ObjectMapper objectMapper;

    /**
     * Constructs {@link TypeConverterValue} with specified <code>objectType</code> and <code>value</code>.
     *
     * @param objectType Type to which to covert value.
     * @param value The value.
     */
    public TypeConverterValue(Class<T> objectType, Value<?> value) {
        this.objectType = objectType;
        this.value = value;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        Object object = value.get();
        val = objectMapper.convertValue(object, objectType);
    }
}
