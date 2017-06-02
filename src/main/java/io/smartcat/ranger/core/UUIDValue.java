package io.smartcat.ranger.core;

import java.util.UUID;

/**
 * Generates random UUID.
 */
public class UUIDValue implements Value<String> {

    @Override
    public String eval() {
        return UUID.randomUUID().toString();
    }
}
