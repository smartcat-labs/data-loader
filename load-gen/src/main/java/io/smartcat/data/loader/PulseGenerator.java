package io.smartcat.data.loader;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.WorkTask;
import io.smartcat.data.loader.tokenbuket.TokenBucket;
import io.smartcat.data.loader.util.AtomicCounter;
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

    private final WorkTask<Object> workTask;

    /**
     * Pulse generator constructor.
     *
     * @param dataCollector data collector
     * @param tokenBucket token bucket
     * @param workTask work task
     * @param collectMetrics collect metrics
     */
    public PulseGenerator(DataCollector dataCollector, TokenBucket tokenBucket, WorkTask<Object> workTask,
            boolean collectMetrics) {

        this.dataCollector = dataCollector;
        this.tokenBucket = tokenBucket;
        this.workTask = workTask;
        this.collectMetrics = collectMetrics;

        workerExecutor = new ThreadPoolExecutor(10, 50, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                (runnable) -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("load-gen-worker-thread-" + LOADGEN_THREAD_COUNT.getAndIncrement());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                });

        pulseExecutor = new ThreadPoolExecutor(10, 10, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
                (runnable) -> {
                    Thread thread = new Thread(runnable);
                    thread.setName("pulse-worker-thread-" + PULSE_THREAD_COUNT.getAndIncrement());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                });
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

}
