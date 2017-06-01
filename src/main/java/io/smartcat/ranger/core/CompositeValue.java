package io.smartcat.ranger.core;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

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
    @SuppressWarnings("unchecked")
    public CompositeValue(Map<String, Value<?>> values) {
        this.values = new HashMap<>(values);
        this.evaluatedValues = new HashMap<>();
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
