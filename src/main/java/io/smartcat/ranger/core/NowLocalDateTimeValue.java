package io.smartcat.ranger.core;

import java.time.LocalDateTime;
/**
 * Generates current date-time as {@link LocalDateTime} object.
 */
public class NowLocalDateTimeValue extends Value<LocalDateTime> {

    @Override
    public void eval() {
        val = LocalDateTime.now();
    }
}
