package io.smartcat.data.loader.worker;

import java.util.function.Consumer;

import io.smartcat.data.loader.api.Worker;

/**
 * Worker which triggers specified consumer every <code>numberOfCalls</code> calls. Useful for logging or monitoring.
 *
 * @param <T> Type of data this worker accepts.
 */
public class CallCountBasedTriggeringWorker<T> implements Worker<T> {

    private final Worker<T> delegate;
    private final long numberOfCalls;
    private final Consumer<Long> consumer;

    private long count = 0;

    /**
     * Constructs worker with specified <code>delegate</code> worker, <code>numberOfCalls</code> and
     * <code>consumer</code>.
     *
     * @param delegate Worker which call will be delegated to, cannot be null.
     * @param numberOfCalls Number of calls after which consumer will be triggered, must be positive number.
     * @param consumer Consumer which will be triggered every <code>numberOfCalls</code> calls with number of calls as
     *            an argument, cannot be null.
     */
    public CallCountBasedTriggeringWorker(Worker<T> delegate, long numberOfCalls, Consumer<Long> consumer) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null.");
        }
        if (numberOfCalls <= 0) {
            throw new IllegalArgumentException("Number of calls must be positive number.");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null.");
        }
        this.delegate = delegate;
        this.numberOfCalls = numberOfCalls;
        this.consumer = consumer;
    }

    @Override
    public void accept(T t) {
        if (++count == numberOfCalls) {
            consumer.accept(count);
            count = 0;
        }
        delegate.accept(t);
    }
}
