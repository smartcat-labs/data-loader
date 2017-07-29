package io.smartcat.ranger.core;

/**
 * Value that always returns specified value.
 *
 * @param <T> Type this value would evaluate to.
 */
public class ConstantValue<T> extends Value<T> {

    /**
     * Constructs constant value which will always return specified <code>value</code>.
     *
     * @param value Value to be returned.
     */
    public ConstantValue(T value) {
        this.val = value;
    }

    /**
     * Helper method to construct {@link ConstantValue}.
     *
     * @param value Value to be returned by created constant value.
     * @param <T> Type this value would evaluate to.
     *
     * @return An instance of {@link ConstantValue}.
     */
    public static <T> ConstantValue<T> of(T value) {
        return new ConstantValue<T>(value);
    }
}
