package io.smartcat.data.loader;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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

    private static final AtomicLong IMPULSE_THREAD_COUNT = new AtomicLong(0);

    private static final AtomicLong COUNTER_THREAD_COUNT = new AtomicLong(0);

    private static final long IMPULSE_PERIOD = 100;

    private static final TimeUnit IMPULSE_TIME_UNIT = TimeUnit.MICROSECONDS;

    private final AtomicCounter impulseCounter = new AtomicCounter();

    private ScheduledThreadPoolExecutor impulseExecutor;

    private ThreadPoolExecutor workerExecutor;

    private Timer timer;

    public ImpulseGenerator() {

        timer = new Timer("impulse-generator-timer");
        timer.scheduleAtFixedRate(new Counter(), 0, 1000);

        impulseExecutor = new ScheduledThreadPoolExecutor(5, (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-impulse-thread-" + IMPULSE_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        workerExecutor = new ThreadPoolExecutor(5, 100, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingDeque<>(), (runnable) -> {
            Thread thread = new Thread(runnable);
            thread.setName("load-gen-counter-thread-" + COUNTER_THREAD_COUNT.getAndIncrement());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        });

        impulseExecutor.scheduleAtFixedRate(new Impulse(), 0, IMPULSE_PERIOD, IMPULSE_TIME_UNIT);
    }

    private class Impulse implements Runnable {
        @Override
        public void run() {
            // TODO do something
            impulseCounter.increment();
            workerExecutor.submit(new Worker());
        }
    }

    private class Counter extends TimerTask {
        @Override
        public void run() {
            LOGGER.info("Generated {} impulses", impulseCounter.getAndReset());
        }
    }

}
