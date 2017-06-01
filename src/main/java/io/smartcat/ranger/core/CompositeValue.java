package io.smartcat.ranger.core;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Composite value containing its child values.
 */
public class CompositeValue implements Value<Map<String, Object>> {

    private final Map<String, Value<?>> values;
    private final Map<String, Object> evaluatedValues;
    private final Map<String, Object> immutableEvaluatedValues;

    /**
     * Constructs composite value with no initial child values.
     */
    public CompositeValue() {
        this(new HashMap<>());
    }

    /**
     * Constructs composite value with specified initial child values.
     *
     * @param values Initial child values.
     */
    @SuppressWarnings("unchecked")
    public CompositeValue(Map<String, Value<?>> values) {
        this.values = new HashMap<>(values);
        this.evaluatedValues = new HashMap<>();
        this.immutableEvaluatedValues = Collections.unmodifiableMap(this.evaluatedValues);
    }

    /**
     * Sets new or updates existing child value of this composite value.
     *
     * @param name Name of the child value.
     * @param value New child value.
     */
    public void setValue(String name, Value<?> value) {
        values.put(name, value);
    }

    @Override
    public Map<String, Object> eval() {
        values.forEach((name, value) -> evaluatedValues.put(name, value.eval()));
        return immutableEvaluatedValues;
    }
}
