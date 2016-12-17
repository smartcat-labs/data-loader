package io.smartcat.data.loader.rules;

import java.util.UUID;

/**
 * Rule for creating random UUIDs.
 */
public class UUIDRule implements Rule<String> {

    @Override
    public boolean isExclusive() {
        // In spite of UUIDs being exclusive by definition (because they are unique), there is no need to calculate
        // precedence for these rules.
        // Thus, for performance reasons, isExclusive returns false.
        return false;
    }

    @Override
    public Rule<String> recalculatePrecedence(Rule<?> exclusiveRule) {
        throw new IllegalArgumentException("no need to calculate rule precedence for UUIDRule.");
    }

    @Override
    public String getRandomAllowedValue() {
        return UUID.randomUUID().toString();
    }

}
