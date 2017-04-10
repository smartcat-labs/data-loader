package io.smartcat.data.loader.worker;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

import io.smartcat.data.loader.api.Worker;

/**
 * Worker which triggers specified consumer approximately every <code>timeInNanos</code> nanoseconds. It uses timer to
 * schedule consumer calls. Can trigger even if worker is not called, but not precise as
 * {@link TimeBasedTriggeringWorker}. Useful for logging or monitoring.
 *
 * @param <T> Type of data this worker accepts.
 *
 * @see TimeBasedTriggeringWorker
 */
public class TimerBasedTriggeringWorker<T> implements Worker<T>, AutoCloseable {

    private final Worker<T> delegate;
    private final ScheduledThreadPoolExecutor timer;

    private AtomicLong count = new AtomicLong(0);

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
    public TimerBasedTriggeringWorker(Worker<T> delegate, long time, TimeUnit unit, Consumer<Long> consumer) {
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
        this.timer = new ScheduledThreadPoolExecutor(1);
        this.timer.scheduleWithFixedDelay(() -> {
            consumer.accept(count.get());
            count.set(0);
        }, TimeUnit.SECONDS.toNanos(1), unit.toNanos(time), TimeUnit.NANOSECONDS);
    }

    @Override
    public void accept(T t) {
        count.incrementAndGet();
        delegate.accept(t);
    }

    @Override
    public void close() {
        timer.shutdown();
    }
}
