package io.smartcat.data.loader;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.DataSource;
import io.smartcat.data.loader.api.WorkTask;
import io.smartcat.data.loader.tokenbuket.FixedRateRefillStrategy;
import io.smartcat.data.loader.tokenbuket.SleepStrategies;
import io.smartcat.data.loader.tokenbuket.TokenBucket;
import io.smartcat.data.loader.util.AtomicCounter;
import io.smartcat.data.loader.util.NoOpDataSource;
import io.smartcat.data.loader.util.NoOpWorkTask;
import io.smartcat.data.loader.util.ThreadPoolExecutorUtil;

/**
 * Pulse generator based on scheduler generating targeted rate.
 */
public class PulseGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PulseGenerator.class);

    private static final AtomicLong LOADGEN_THREAD_COUNT = new AtomicLong(0);

    private static final AtomicLong PULSE_THREAD_COUNT = new AtomicLong(0);

    private volatile boolean isRunning = false;

    private final AtomicCounter pulseCounter = new AtomicCounter();

    private final ThreadPoolExecutor workerExecutor;

    private final ThreadPoolExecutor pulseExecutor;

    private final TokenBucket tokenBucket;

    private final DataCollector dataCollector;

    private boolean collectMetrics;

    private final WorkTask workTask;

    private PulseGenerator(PulseGeneratorBuilder builder) {

        this.collectMetrics = builder.collectMetrics;
        this.dataCollector = new DataCollector(builder.dataSource, (int) (builder.targetRate * 1.2));

        workerExecutor = new ThreadPoolExecutor(builder.workerThreadPoolCoreSize, builder.workerThreadPoolMaxSize, 100L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-worker-thread-" + LOADGEN_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        pulseExecutor = new ThreadPoolExecutor(builder.pulseThreadPoolCoreSize, builder.pulseThreadPoolMaxSize, 10L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("pulse-worker-thread-" + PULSE_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        tokenBucket = new TokenBucket(builder.targetRate, 0,
                new FixedRateRefillStrategy(builder.targetRate, 1, TimeUnit.SECONDS),
                SleepStrategies.NANOSECOND_SLEEP_STRATEGY(1));

        this.workTask = builder.workTask;
    }

    /**
     * Start pulse generator.
     */
    public void start() {
        this.isRunning = true;
        this.dataCollector.start();
        ThreadPoolExecutorUtil.fillThreadPool(pulseExecutor, new Pulse(workTask));
    }

    /**
     * Stop pulse generator.
     *
     * @throws InterruptedException Interrupted exception
     */
    public void stop() throws InterruptedException {
        this.isRunning = false;
        this.dataCollector.stop();
        this.pulseExecutor.shutdown();
        this.workerExecutor.shutdown();
    }

    /**
     * Set target rate.
     *
     * @param targetRate target rate
     */
    public void setTargetRate(long targetRate) {
        // TODO: Handle rate changes through RefillStrategy implementation
    }

    /**
     * Load generator pulse which triggers work task execution.
     */
    private class Pulse implements Runnable {

        private WorkTask workTask;

        public Pulse(WorkTask workTask) {
            this.workTask = workTask;
        }

        @Override
        public void run() {
            while (isRunning) {
                tokenBucket.get();
                if (dataCollector.queueSize() > 0) {

                    int value = dataCollector.poll();
                    workerExecutor.submit(new Worker(workTask, value));

                    if (collectMetrics) {
                        pulseCounter.increment();
                    }
                }
            }
        }
    }

    /**
     * Get pulse count and reset counter.
     *
     * @return pulse count since last reset
     */
    public long getPulseCount() {
        return pulseCounter.getAndReset();
    }

    /**
     * Pulse generator builder class.
     */
    public static class PulseGeneratorBuilder {

        private int workerThreadPoolCoreSize = 10;

        private int workerThreadPoolMaxSize = 500;

        private int pulseThreadPoolCoreSize = 10;

        private int pulseThreadPoolMaxSize = 10;

        private long targetRate = 1000;

        private boolean collectMetrics = false;

        private DataSource dataSource = new NoOpDataSource();

        private WorkTask workTask = new NoOpWorkTask();

        /**
         * Define a worker thread pool core size.
         *
         * @param workerThreadPoolCoreSize worker thread pool core size
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withWorkerThreadPoolCoreSize(int workerThreadPoolCoreSize) {
            this.workerThreadPoolCoreSize = workerThreadPoolCoreSize;
            return this;
        }

        /**
         * Define a worker thread pool max size.
         *
         * @param workerThreadPoolMaxSize worker thread pool max size
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withWorkerThreadPoolMaxSize(int workerThreadPoolMaxSize) {
            this.workerThreadPoolMaxSize = workerThreadPoolMaxSize;
            return this;
        }

        public PulseGeneratorBuilder withPulseThreadPoolCoreSize(int pulseThreadPoolCoreSize) {
            this.pulseThreadPoolCoreSize = pulseThreadPoolCoreSize;
            return this;
        }

        public PulseGeneratorBuilder withPulseThreadPoolMaxSize(int pulseThreadPoolMaxSize) {
            this.pulseThreadPoolMaxSize = pulseThreadPoolMaxSize;
            return this;
        }

        /**
         * Define a target rate.
         *
         * @param targetRate target rate
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withTargetRate(long targetRate) {
            this.targetRate = targetRate;
            return this;
        }

        /**
         * Define if metrics are being collected.
         *
         * @param collectMetrics collect metrics
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withMetrics(boolean collectMetrics) {
            this.collectMetrics = collectMetrics;
            return this;
        }

        /**
         * Define a DataSource implementation providing data for WorkTask execution.
         *
         * @param dataSource {@code io.smartcat.data.loader.api.DataSource} implementation
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        /**
         * Define a WorkTask implementation that will be executed at a given rate.
         *
         * @param workTask {@code io.smartcat.data.loader.api.WorkTask} implementation
         * @return {@code io.smartcat.data.loader.PulseGeneratorBuilder} instance
         */
        public PulseGeneratorBuilder withWorkTask(WorkTask workTask) {
            this.workTask = workTask;
            return this;
        }

        /**
         * Build {@code io.smartcat.data.loader.PulseGenerator} instance.
         *
         * @return {@code io.smartcat.data.loader.PulseGenerator} instance
         */
        public PulseGenerator build() {
            return new PulseGenerator(this);
        }

    }

}
