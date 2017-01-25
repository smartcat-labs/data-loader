package io.smartcat.data.loader;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.DataSource;
import io.smartcat.data.loader.api.WorkTask;
import io.smartcat.data.loader.util.AtomicCounter;
import io.smartcat.data.loader.util.NoOpDataSource;
import io.smartcat.data.loader.util.NoOpWorkTask;
import io.smartcat.data.loader.util.RateLimiter;

/**
 * Impulse generator based on scheduler generating targeted rate.
 */
public class ImpulseGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImpulseGenerator.class);

    private static final AtomicLong COUNTER_THREAD_COUNT = new AtomicLong(0);

    private final AtomicCounter impulseCounter = new AtomicCounter();

    private ThreadPoolExecutor workerExecutor;

    private RateLimiter limiter;

    private Thread impulseGenerator;

    private boolean collectMetrics;

    private DataCollector dataCollector;

    private ImpulseGenerator(ImpulseGeneratorBuilder builder) {

        this.collectMetrics = builder.collectMetrics;
        this.dataCollector = new DataCollector(builder.dataSource);

        workerExecutor = new ThreadPoolExecutor(builder.workerThreadPoolCoreSize, builder.workerThreadPoolMaxSize, 100L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-worker-thread-" + COUNTER_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        limiter = new RateLimiter(1000, builder.targetRate);

        impulseGenerator = new Thread(new Impulse(builder.workTask));
    }

    /**
     * Start impulse generator.
     *
     * @param targetRate target rate
     */
    public void start(double targetRate) {
        this.limiter.setRate(targetRate);
        this.dataCollector.start();
        this.impulseGenerator.start();
    }

    /**
     * Stop impulse generator.
     *
     * @throws InterruptedException Interrupted exception
     */
    public void stop() throws InterruptedException {
        this.dataCollector.stop();
        this.impulseGenerator.join();
    }

    /**
     * Set target rate.
     *
     * @param targetRate target rate
     */
    public void setTargetRate(long targetRate) {
        limiter.setRate(targetRate);
    }

    private class Impulse implements Runnable {

        private WorkTask workTask;

        public Impulse(WorkTask workTask) {
            this.workTask = workTask;
        }

        @Override
        public void run() {
            while (true) {
                limiter.limit();
                if (dataCollector.queueSize() > 0) {

                    int value = dataCollector.poll();
                    workerExecutor.submit(new Worker(workTask, value));

                    if (collectMetrics) {
                        impulseCounter.increment();
                    }
                }
            }
        }
    }

    /**
     * Get impulse count and reset counter.
     *
     * @return impulse count since last reset
     */
    public long getImpulseCount() {
        return impulseCounter.getAndReset();
    }

    /**
     * Impulse generator builder class
     */
    public static class ImpulseGeneratorBuilder {

        private int workerThreadPoolCoreSize = 10;

        private int workerThreadPoolMaxSize = 500;

        private double targetRate = 1000;

        private boolean collectMetrics = false;

        private DataSource dataSource = new NoOpDataSource();

        private WorkTask workTask = new NoOpWorkTask();

        public ImpulseGeneratorBuilder withWorkerThreadPoolCoreSize(int workerThreadPoolCoreSize) {
            this.workerThreadPoolCoreSize = workerThreadPoolCoreSize;
            return this;
        }

        public ImpulseGeneratorBuilder withWorkerThreadPoolMaxSize(int workerThreadPoolMaxSize) {
            this.workerThreadPoolMaxSize = workerThreadPoolMaxSize;
            return this;
        }

        public ImpulseGeneratorBuilder withTargetRate(double targetRate) {
            this.targetRate = targetRate;
            return this;
        }

        public ImpulseGeneratorBuilder withMetrics(boolean collectMetrics) {
            this.collectMetrics = collectMetrics;
            return this;
        }

        public ImpulseGeneratorBuilder withDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
            return this;
        }

        public ImpulseGeneratorBuilder withWorkTask(WorkTask workTask) {
            this.workTask = workTask;
            return this;
        }

        public ImpulseGenerator build() {
            return new ImpulseGenerator(this);
        }

    }

}
