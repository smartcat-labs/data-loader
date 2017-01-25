package io.smartcat.data.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple tocket bucket rate limiter implementation
 */
public class RateLimiter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RateLimiter.class);

    private static final double MIN_WAIT_S = 0.005;
    private static final double MAX_WAIT_S = 0.1;

    private double capacity;
    private double rate;

    private double bucket;
    private long lastFill;

    /**
     * Create a new rate limiter
     *
     * @param capacity the maximum capacity
     * @param rate     the maximum rate
     */
    public RateLimiter(int capacity, double rate) {
        this.rate = rate;
        this.capacity = capacity;

        bucket = 0;
        lastFill = System.currentTimeMillis();
    }

    /**
     * Update the bucket fill level.
     */
    private synchronized void updateBucket() {
        long time = System.currentTimeMillis();

        if (rate == 0) {
            bucket = capacity;
        } else {
            bucket += rate * (time - lastFill) / 1000;
            if (bucket > capacity) {
                bucket = capacity;
            }
        }
        lastFill = time;
    }

    /**
     * Wait until execution possible
     */
    public void limit() {
        while (true) {
            updateBucket();
            synchronized (this) {
                if (bucket >= 0) {
                    break;
                }
            }
            double waitTime = -bucket / rate;
            if (waitTime < MIN_WAIT_S || waitTime > MAX_WAIT_S) {
                waitTime = MIN_WAIT_S;
            }
            try {
                Thread.sleep((long) (waitTime * 1000));
            } catch (InterruptedException ex) {
                LOGGER.error("Error in rate limiter", ex);
            }
        }
        synchronized (this) {
            bucket -= 1;
        }
    }

    /**
     * Check if the rate is available
     *
     * @return rate available
     */
    public boolean rateAvailable() {
        updateBucket();
        synchronized (this) {
            if (bucket >= 0) {
                bucket -= 1;
                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Set the maximum rate.
     *
     * @param rate the rate in req/s
     */
    public synchronized void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Get maximum rate.
     *
     * @return maximum rate in req/s
     */
    public double getRate() {
        return this.rate;
    }
}
