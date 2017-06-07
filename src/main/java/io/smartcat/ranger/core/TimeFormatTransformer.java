package io.smartcat.ranger.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Creates a formatted string using a specified time format and long value representing time in epoch milliseconds.
 */
public class TimeFormatTransformer extends Transformer<String> {

    private final Value<Long> value;
    private final DateTimeFormatter formatter;

    /**
     * Creates a formatted string with specified <code>format</code> and <code>value</code>.
     * Format can be any date format (e.g. 'YYYY-MM-dd', 'dd.MM.YYYY-hh:mm:ss').
     *
     * @param format Format string.
     * @param value Long value representing time in epoch milliseconds.
     */
    public TimeFormatTransformer(String format, Value<Long> value) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format string cannot be null nor empty.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        this.value = value;
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        long epochMilli = value.get();
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        val = date.format(formatter);
    }
}
