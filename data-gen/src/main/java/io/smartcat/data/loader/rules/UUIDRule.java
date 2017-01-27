package io.smartcat.data.loader.rules;

import java.util.UUID;

/**
 * Rule for creating random UUIDs.
 */
public class UUIDRule implements Rule<String> {

    @Override
    public String getRandomAllowedValue() {
        return UUID.randomUUID().toString();
    }

}
