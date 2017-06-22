package io.smartcat.ranger.core;

import java.util.Date;

/**
 * Generates current date-time as {@link Date} object.
 */
public class NowDateValue extends Value<Date> {

    @Override
    public void eval() {
        val = new Date();
    }
}
