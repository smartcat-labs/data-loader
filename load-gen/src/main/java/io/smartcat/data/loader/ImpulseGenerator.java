package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
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

    private static final int WORKER_THREAD_POOL_CORE_SIZE = 10;

    private static final int WORKER_THREAD_POOL_MAX_SIZE = 250;

    private static final long DEFAULT_TARGET_RATE = 1000;

    private final AtomicCounter impulseCounter = new AtomicCounter();

    private ThreadPoolExecutor workerExecutor;

    private Timer impulseTimer;

    private RateLimiter limiter;

    private Thread impulseGenerator;

    public ImpulseGenerator() {

        impulseTimer = new Timer("impulse-generator-timer");
        impulseTimer.scheduleAtFixedRate(new Counter(), 0, 1000);

        workerExecutor = new ThreadPoolExecutor(WORKER_THREAD_POOL_CORE_SIZE, WORKER_THREAD_POOL_MAX_SIZE, 100L,
                TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-worker-thread-" + COUNTER_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        limiter = new RateLimiter(1000, DEFAULT_TARGET_RATE);
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> limiter.tick(), 0, 100, TimeUnit.MICROSECONDS);

        impulseGenerator = new Thread(new Impulse());
    }

    public void start(long targetRate) {
        limiter.setRate(targetRate);
        impulseGenerator.start();
    }

    public void stop() throws InterruptedException {
        impulseGenerator.join();
    }

    public void setTargetRate(long targetRate) {
        limiter.setRate(targetRate);
    }

    private class Impulse implements Runnable {
        @Override
        public void run() {
            while (true) {
                limiter.limit();
                impulseCounter.increment();
                workerExecutor.submit(new Worker());
            }
        }
    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.debug("Generated {} impulses", impulseCounter.getAndReset());
        }
    }

}
