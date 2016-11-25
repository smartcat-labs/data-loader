package io.smartcat.data.loader;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Atomic counter implementation.
 */
public class AtomicCounter {

    private final AtomicLong counter;

    /**
     * Creates new {@link AtomicCounter} instance and set value to {@code 0}.
     */
    public AtomicCounter() {
        this(0);
    }

    /**
     * Creates new {@link AtomicCounter} instance and set value to provided value.
     *
     * @param init initial value
     */
    public AtomicCounter(final long init) {
        counter = new AtomicLong(init);
    }

    /**
     * Increment value by {@code 1}.
     */
    public void increment() {
        counter.incrementAndGet();
    }

    /**
     * Decrement value by {@code 1}.
     */
    public void decrement() {
        counter.decrementAndGet();
    }

    /**
     * Get current value.
     *
     * @return current value
     */
    public long get() {
        return counter.get();
    }

    /**
     * Get the current value then reset counter to {@code 0}.
     *
     * @return current value
     */
    public long getAndReset() {
        return counter.getAndSet(0);
    }

    /**
     * Get the current value then reset counter to {@code init} value.
     *
     * @param init Value to set counter to.
     * @return current value
     */
    public long getAndReset(final long init) {
        return counter.getAndSet(init);
    }

}