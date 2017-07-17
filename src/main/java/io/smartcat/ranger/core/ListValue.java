package io.smartcat.ranger.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generates list out of specified values.
 *
 * @param <T> Type which evaluated list will contain.
 */
public class ListValue<T> extends Value<List<T>> {

    private final List<Value<T>> values;

    /**
     * Constructs list value out of specified values.
     *
     * @param values Values which will constitute list.
     */
    public ListValue(List<Value<T>> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("List of values cannot be null nor empty.");
        }
        this.values = new ArrayList<>(values);
    }

    @Override
    public void reset() {
        super.reset();
        for (Value<T> value : values) {
            value.reset();
        }
    }

    @Override
    protected void eval() {
        List<T> result = new ArrayList<>();
        for (Value<T> value : values) {
            result.add(value.get());
        }
        val = Collections.unmodifiableList(result);
    }
}
