package io.smartcat.ranger.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns values in order specified within <code>values</code> parameter. When end is reached, it starts over from the
 * beginning.
 *
 * @param <T> Type this value would evaluate to.
 */
public class CircularValue<T> extends Value<T> {

    private final List<Value<T>> values;
    private final int size;
    private int currentIndex;

    /**
     * Constructs circular value with specified <code>values</code>.
     *
     * @param values List of possible values.
     */
    public CircularValue(List<Value<T>> values) {
        if (values == null || values.isEmpty()) {
            throw new IllegalArgumentException("List of values cannot be null nor empty.");
        }
        this.values = new ArrayList<>(values);
        this.size = this.values.size();
        this.currentIndex = -1;
    }

    @Override
    public void reset() {
        super.reset();
        values.get(nextIndex()).reset();
    }

    @Override
    protected void eval() {
        currentIndex = nextIndex();
        val = values.get(currentIndex).get();
    }

    private int nextIndex() {
        if (currentIndex == size - 1) {
            return 0;
        } else {
            return currentIndex + 1;
        }
    }
}
