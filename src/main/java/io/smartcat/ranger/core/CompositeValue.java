package io.smartcat.ranger.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Composite value containing its child values.
 */
public class CompositeValue extends Value<Map<String, Object>> {

    private final Map<String, Value<?>> values;
    private final Map<String, Object> evaluatedValues;

    /**
     * Constructs composite value with specified initial child values.
     *
     * @param values Initial child values.
     */
    public CompositeValue(Map<String, Value<?>> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("Values map cannot be null nor empty.");
        }
        this.values = new LinkedHashMap<>(values);
        this.evaluatedValues = new LinkedHashMap<>();
        this.val = Collections.unmodifiableMap(this.evaluatedValues);
    }

    @Override
    public void reset() {
        super.reset();
        values.values().forEach(v -> v.reset());
    }

    @Override
    protected void eval() {
        values.forEach((name, value) -> {
            evaluatedValues.put(name, value.get());
        });
    }
}
