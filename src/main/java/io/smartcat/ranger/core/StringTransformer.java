package io.smartcat.ranger.core;

import java.util.List;

import org.slf4j.helpers.MessageFormatter;

/**
 * Creates a formatted string using the specified format string and values.
 */
public class StringTransformer implements Transformer<String> {

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
        this.format = format;
        this.values = values;
        this.calculatedValues = new Object[this.values.size()];
    }

    @Override
    public String eval() {
        evaluateValues();
        return MessageFormatter.arrayFormat(format, calculatedValues).getMessage();
    }

    private void evaluateValues() {
        for (int i = 0; i < values.size(); i++) {
            Value<?> value = values.get(i);
            calculatedValues[i] = value.eval();
        }
    }
}
