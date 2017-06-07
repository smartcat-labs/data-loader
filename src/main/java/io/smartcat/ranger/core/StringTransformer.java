package io.smartcat.ranger.core;

import java.util.List;

import org.slf4j.helpers.MessageFormatter;

/**
 * Creates a formatted string using the specified format string and values.
 */
public class StringTransformer extends Transformer<String> {

    private final String format;
    private final List<Value<?>> values;
    private final Object[] calculatedValues;

    /**
     * Constructs string transformer with specified <code>format</code> string and list of <code>values</code>.
     * Placeholder for value is defined as '{}', first placeholder uses first value, second, second value, and so on.
     *
     * @param format Format string.
     * @param values List of values.
     */
    public StringTransformer(String format, List<Value<?>> values) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format string cannot be null nor empty.");
        }
        if (values == null) {
            throw new IllegalArgumentException("values cannot be null nor empty.");
        }
        this.format = format;
        this.values = values;
        this.calculatedValues = new Object[this.values.size()];
    }

    @Override
    public void reset() {
        super.reset();
        values.forEach(v -> v.reset());
    }

    @Override
    protected void eval() {
        calculateValues();
        val = MessageFormatter.arrayFormat(format, calculatedValues).getMessage();
    }

    private void calculateValues() {
        for (int i = 0; i < values.size(); i++) {
            Value<?> value = values.get(i);
            calculatedValues[i] = value.get();
        }
    }
}
