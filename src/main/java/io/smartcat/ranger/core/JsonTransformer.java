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
     *
     * @param value Value which will be transformed into its JSON representation.
     */
    public JsonTransformer(Value<?> value) {
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
        try {
            val = objectMapper.writeValueAsString(value.get());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
