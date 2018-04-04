package io.smartcat.ranger.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Transforms value into its JSON representation.
 */
public class JsonTransformer extends Transformer<String> {

    private final Value<?> value;
    private final ObjectMapper objectMapper;

    /**
     * Constructs JSON transformer with specified <code>value</code>.
     * New default instance of {@link ObjectMapper} will be used.
     *
     * @param value Value which will be transformed into its JSON representation.
     */
    public JsonTransformer(Value<?> value) {
        this(value, new ObjectMapper());
    }

    /**
     * Constructs JSON transformer with specified <code>value</code> and <code>objectMapper</code>.
     *
     * @param value Value which will be transformed into its JSON representation.
     * @param objectMapper Object mapper which will be used to map value to JSON.
     */
    public JsonTransformer(Value<?> value, ObjectMapper objectMapper) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        if (objectMapper == null) {
            throw new IllegalArgumentException("Object mapper cannot be null.");
        }
        this.value = value;
        this.objectMapper = objectMapper;
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        try {
            val = objectMapper.writeValueAsString(value.get());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
