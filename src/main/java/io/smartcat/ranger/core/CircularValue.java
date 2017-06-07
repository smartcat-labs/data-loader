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
        this.values = new ArrayList<>(values);
        this.size = this.values.size();
        this.currentIndex = 0;
    }

    @Override
    public void reset() {
        super.reset();
        values.get(incrementIndex()).reset();
    }

    @Override
    protected void eval() {
        val = values.get(currentIndex).get();
    }

    private int incrementIndex() {
        if (currentIndex == size - 1) {
            currentIndex = 0;
        } else {
            currentIndex++;
        }
        return currentIndex;
    }
}
