package io.smartcat.data.loader.tokenbuket;

import java.util.concurrent.TimeUnit;

/**
 * Predefined sleep strategies based on system time.
 */
public class SleepStrategies {

    public static final io.smartcat.data.loader.tokenbuket.SleepStrategy BUSY_SLEEP_STRATEGY() {
        return () -> {

        };
    }

    public static final io.smartcat.data.loader.tokenbuket.SleepStrategy NANOSECOND_SLEEP_STRATEGY(int duration) {
        return () -> {
            try {
                TimeUnit.NANOSECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    public static final io.smartcat.data.loader.tokenbuket.SleepStrategy MICROSECOND_SLEEP_STRATEGY(int duration) {
        return () -> {
            try {
                TimeUnit.MICROSECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

    public static final io.smartcat.data.loader.tokenbuket.SleepStrategy MILLISECOND_SLEEP_STRATEGY(int duration) {
        return () -> {
            try {
                TimeUnit.MILLISECONDS.sleep(duration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };
    }

}
