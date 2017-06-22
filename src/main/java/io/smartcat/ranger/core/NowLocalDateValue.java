package io.smartcat.ranger.core;

import java.time.LocalDate;

/**
 * Generates current date as {@link LocalDate} object.
 */
public class NowLocalDateValue extends Value<LocalDate> {

    @Override
    public void eval() {
        val = LocalDate.now();
    }
}
