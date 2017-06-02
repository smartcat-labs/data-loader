package io.smartcat.ranger.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Transforms value into its JSON representation.
 */
public class JsonTransformer implements Transformer<String> {

    private final Value<?> value;
    private final ObjectMapper objectMapper;

    /**
     * Constructs JSON transformer with specified <code>value</code>.
     *
     * @param value Value which will be transformed into its JSON representation.
     */
    public JsonTransformer(Value<?> value) {
        this.value = value;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String eval() {
        try {
            return objectMapper.writeValueAsString(value.eval());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
