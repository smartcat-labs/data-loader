package io.smartcat.ranger.core;

import java.util.UUID;

/**
 * Generates random UUID.
 */
public class UUIDValue extends Value<String> {

    @Override
    public void eval() {
        val = UUID.randomUUID().toString();
    }
}
