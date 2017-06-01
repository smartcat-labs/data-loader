package io.smartcat.ranger.core;

/**
 * Value that always returns null.
 */
public class NullValue implements Value<Object> {

    @Override
    public Object eval() {
        return null;
    }
}
