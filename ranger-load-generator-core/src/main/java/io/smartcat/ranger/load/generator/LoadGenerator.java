package io.smartcat.ranger.load.generator;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.ranger.load.generator.api.DataSource;
import io.smartcat.ranger.load.generator.api.RateGenerator;
import io.smartcat.ranger.load.generator.api.Worker;
import io.smartcat.ranger.load.generator.worker.AsyncWorker;

/**
 * Load generator used to execute work tasks with data from provided data source.
 *
 * @param <T> Type of data which will be used.
 */
public class LoadGenerator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadGenerator.class);
    private static final long NANOS_IN_SECOND = TimeUnit.SECONDS.toNanos(1);
    private static final long TICK_PERIOD_IN_NANOS = 1000;
    private static final int DEFAULT_QUEUE_CAPACITY = 10_000;
    private static final int DEFAULT_THREAD_COUNT_COEFFICIENT = 2;

    private final DataSource<T> dataSource;
    private final RateGenerator rateGenerator;
    private final Worker<T> worker;

    private AtomicBoolean terminate = new AtomicBoolean(false);

    /**
     * Constructs load generator with specified <code>dataSource</code>, <code>rateGenerator</code> and
     * <code>worker</code>.
     *
     * @param dataSource Data source from which load generator polls data.
     * @param rateGenerator Rate generator which generates rate based on time point.
     * @param worker Worker which accepts data polled from <code>dataSource</code> at rate provided by
     *            <code>rateGenerator</code>.
     */
    public LoadGenerator(DataSource<T> dataSource, RateGenerator rateGenerator, Worker<T> worker) {
        this.dataSource = dataSource;
        this.rateGenerator = rateGenerator;
        this.worker = configureWorker(worker);
    }

    /**
     * Runs load generator.
     *
     * @throws IllegalStateException When run is attempted after load generator was terminated.
     */
    public void run() {
        checkState();
        LOGGER.info("Load generator started.");
        long beginning = System.nanoTime();
        long previous = beginning;
        infiniteWhile: while (true) {
            if (terminate.get()) {
                LOGGER.info("Termination signal detected. Terminating load generator...");
                break;
            }
            long now = System.nanoTime();
            long fromBeginning = now - beginning;
            long elapsed = now - previous;
            long rate = rateGenerator.getRate(fromBeginning);
            long normalizedRate = normalizeRate(elapsed, rate);
            if (normalizedRate > 0) {
                previous += calculateConsumedTime(normalizedRate, rate);
            }
            for (int i = 0; i < normalizedRate; i++) {
                if (!dataSource.hasNext(fromBeginning)) {
                    LOGGER.info("Reached end of data source. Terminating load generator...");
                    terminate.set(true);
                    break infiniteWhile;
                }
                T data = dataSource.getNext(fromBeginning);
                worker.accept(data);
            }
        }
        LOGGER.info("Load generator terminated.");
    }

    /**
     * Stops load generator.
     */
    public void terminate() {
        terminate.set(true);
        LOGGER.info("Termination signal sent.");
    }

    /**
     * Configures worker, default implementation will wrap worker into {@link AsyncWorker} if not already wrapped.
     *
     * @param worker Worker to be configured.
     * @return Instance of {@link Worker} to be used by this load generator.
     */
    protected Worker<T> configureWorker(Worker<T> worker) {
        if (worker instanceof AsyncWorker) {
            return worker;
        }
        return new AsyncWorker<>(worker, DEFAULT_QUEUE_CAPACITY, (x) -> {
        }, true, Runtime.getRuntime().availableProcessors() * DEFAULT_THREAD_COUNT_COEFFICIENT);
    }

    private void checkState() {
        if (terminate.get()) {
            throw new IllegalStateException("Load generator is stopped and cannot be started again.");
        }
    }

    private long normalizeRate(long elapsed, long rate) {
        if (elapsed < TICK_PERIOD_IN_NANOS) {
            return 0;
        }
        return elapsed * rate / NANOS_IN_SECOND;
    }

    private long calculateConsumedTime(long normalizedRate, long rate) {
        return normalizedRate * NANOS_IN_SECOND / rate;
    }
}
