package io.smartcat.data.loader;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private ImpulseGenerator(ImpulseGeneratorBuilder builder) {

        this.collectMetrics = builder.collectMetrics;

        workerExecutor = new ThreadPoolExecutor(builder.workerThreadPoolCoreSize, builder.workerThreadPoolMaxSize, 100L,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-worker-thread-" + COUNTER_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        limiter = new RateLimiter(1000, builder.targetRate);

        impulseGenerator = new Thread(new Impulse());
    }

    /**
     * Start impulse generator.
     *
     * @param targetRate target rate
     */
    public void start(long targetRate) {
        limiter.setRate(targetRate);
        impulseGenerator.start();
    }

    /**
     * Stop impulse generator.
     *
     * @throws InterruptedException exception
     */
    public void stop() throws InterruptedException {
        impulseGenerator.join();
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
        @Override
        public void run() {
            while (true) {
                limiter.limit();
                workerExecutor.submit(new Worker());
                if (collectMetrics) {
                    impulseCounter.increment();
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

        private int workerThreadPoolMaxSize = 250;

        private double targetRate = 1000;

        private boolean collectMetrics = false;

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

        public ImpulseGenerator build() {
            return new ImpulseGenerator(this);
        }

    }

}
