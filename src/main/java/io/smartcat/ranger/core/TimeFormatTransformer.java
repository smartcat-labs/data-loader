package io.smartcat.ranger.core;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Creates a formatted string using a specified time format and long value representing time in epoch milliseconds.
 */
public class TimeFormatTransformer extends Transformer<String> {

    private final Value<?> value;
    private final DateTimeFormatter dateTimeFormatter;
    private final SimpleDateFormat dateFormater;

    /**
     * Creates a formatted string with specified <code>format</code> and <code>value</code>. Format can be any date
     * format (e.g. 'YYYY-MM-dd', 'dd.MM.YYYY-hh:mm:ss').
     *
     * @param format Format string.
     * @param value Value representing time, can return {@link Long}, {@link Date}, {@link LocalDate} or
     *            {@link LocalDateTime}.
     * @param <T> Type value parameter returns.
     */
    public <T> TimeFormatTransformer(String format, Value<T> value) {
        if (format == null || format.isEmpty()) {
            throw new IllegalArgumentException("Format string cannot be null nor empty.");
        }
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }
        this.value = value;
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        this.dateFormater = new SimpleDateFormat(format);
    }

    @Override
    public void reset() {
        super.reset();
        value.reset();
    }

    @Override
    protected void eval() {
        Object generatedValue = value.get();
        if (generatedValue instanceof Long) {
            long epochMilli = (long) generatedValue;
            LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneId.systemDefault());
            val = date.format(dateTimeFormatter);
        }
        if (generatedValue instanceof Date) {
            Date date = (Date) generatedValue;
            val = dateFormater.format(date);
        }
        if (generatedValue instanceof LocalDate) {
            LocalDate date = (LocalDate) generatedValue;
            val = date.format(dateTimeFormatter);
        }
        if (generatedValue instanceof LocalDateTime) {
            LocalDateTime date = (LocalDateTime) generatedValue;
            val = date.format(dateTimeFormatter);
        }
    }
}
