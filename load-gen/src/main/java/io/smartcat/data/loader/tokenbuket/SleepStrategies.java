package io.smartcat.data.loader.tokenbuket;

import java.util.concurrent.TimeUnit;

/**
 * Predefined sleep strategies based on system time.
 */
public class SleepStrategies {

    private SleepStrategies() {

    }

    /**
     * Busy sleep strategy implementation that doesn't sleep or block the thread.
     *
     * @return sleep strategy
     */
    public static final SleepStrategy busySleepStrategy() {
        return () -> {

        };
    }

    /**
     * Nanosecond based sleep strategy.
     *
     * @param duration nanosecond duration
     * @return sleep strategy
     */
    public static final SleepStrategy nanosecondSleepStrategy(int duration) {
        return () -> {
            try {
                TimeUnit.NANOSECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    /**
     * Microsecond based sleep strategy.
     *
     * @param duration microsecond duration
     * @return sleep strategy
     */
    public static final SleepStrategy microsecondSleepStrategy(int duration) {
        return () -> {
            try {
                TimeUnit.MICROSECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    /**
     * Millisecond based sleep strategy.
     *
     * @param duration millisecond duration
     * @return sleep strategy
     */
    public static final SleepStrategy millisecondSleepStrategy(int duration) {
        return () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

}
