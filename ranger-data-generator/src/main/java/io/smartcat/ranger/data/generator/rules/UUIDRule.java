package io.smartcat.ranger.data.generator.rules;

import java.util.UUID;

/**
 * Rule for creating random UUIDs.
 */
public class UUIDRule implements Rule<String> {

    @Override
    public String next() {
        return UUID.randomUUID().toString();
    }
}
