package io.smartcat.data.loader.worker;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import io.smartcat.data.loader.api.Worker;

/**
 * Worker which triggers specified trigger every <code>timeInNanos</code> nanoseconds. It uses time difference to
 * trigger calls. Can not trigger if worker is not called, but if triggered, it is more precise than
 * {@link TimerBasedTriggeringWorker}. Useful for logging or monitoring.
 *
 * @param <T> Type of data this worker accepts.
 *
 * @see TimerBasedTriggeringWorker
 */
public class TimeBasedTriggeringWorker<T> implements Worker<T> {

    private final Worker<T> delegate;
    private final long timeInNanos;
    private final Consumer<Long> consumer;

    private long last = 0;
    private long count = 0;

    /**
     * Constructs worker with specified <code>delegate</code> worker, <code>time</code>, <code>unit</code> and
     * <code>consumer</code>.
     *
     * @param delegate Worker which call will be delegated to, cannot be null.
     * @param time Number of time units after which consumer will be triggered, must be positive number.
     * @param unit Unit of time.
     * @param consumer Consumer which will be triggered every <code>numberOfCalls</code> calls with number of calls as
     *            an argument, cannot be null.
     */
    public TimeBasedTriggeringWorker(Worker<T> delegate, long time, TimeUnit unit, Consumer<Long> consumer) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null.");
        }
        if (time <= 0) {
            throw new IllegalArgumentException("Time in nanoseconds must be positive number.");
        }
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null.");
        }
        this.delegate = delegate;
        this.timeInNanos = unit.toNanos(time);
        this.consumer = consumer;
    }

    @Override
    public void accept(T t) {
        long now = System.nanoTime();
        if (last == 0) {
            last = now;
        }
        if ((now - last) > timeInNanos) {
            consumer.accept(count);
            count = 0;
            last = now;
        } else {
            count++;
        }
        delegate.accept(t);
    }
}
