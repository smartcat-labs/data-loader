package io.smartcat.ranger.core;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Creates a formatted string using a specified time format and long value representing time in epoch milliseconds.
 */
public class TimeFormatTransformer implements Transformer<String> {

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
        this.value = value;
        this.formatter = DateTimeFormatter.ofPattern(format);
    }

    @Override
    public String eval() {
        long epochMilli = value.eval();
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
        return date.format(formatter);
    }
}
