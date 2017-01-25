package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A simple BlockingQueue based rate limiter.
 * Usage: call limit() to throttle the current thread (blocks)
 * and call tick() at regular intervals from a separate thread.
 */
public class RateLimiter {
    private long fillPeriod;
    private final BlockingQueue<Object> queue;
    private long timer;

    /**
     * Create a simple blocking queue based rate limiter with a
     * certain capacity and fill rate. Be careful when handling
     * lots of requests with a high capacity as memory usage
     * scales with capacity.
     *
     * @param capacity capacity before rate limiting kicks in
     * @param rate     rate limit in allowed calls per second
     */
    public RateLimiter(int capacity, double rate) {
        if (rate <= 0) {
            this.fillPeriod = Long.MAX_VALUE;
        } else {
            this.fillPeriod = (long) (1000000000L / rate);
        }
        this.queue = new ArrayBlockingQueue<Object>(capacity);
        this.timer = System.nanoTime();
    }

    /**
     * Set rate
     *
     * @param rate rate limit in allowed calls per second
     */
    public synchronized void setRate(double rate) {
        if (rate <= 0) {
            this.fillPeriod = Long.MAX_VALUE;
        } else {
            this.fillPeriod = (long) (1000000000L / rate);
        }
    }

    /**
     * Tick the rate limiter, advancing the timer and possibly
     * unblocking calls to limit()
     */
    public synchronized void tick() {
        long elapsedTime = System.nanoTime() - timer;
        int numToRemove = (int) (elapsedTime / fillPeriod);

        // advance timer
        timer += fillPeriod * numToRemove;

        List<Object> discardedObjects = new ArrayList<Object>(numToRemove);
        queue.drainTo(discardedObjects, numToRemove);
    }

    /**
     * A call to this method blocks when it is called too often
     * (depleted capacity).
     *
     * @return false when interrupted, otherwise true
     */
    public boolean limit() {
        try {
            queue.put(new Object());
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}
