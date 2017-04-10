package io.smartcat.data.loader.tokenbuket;

import java.util.concurrent.TimeUnit;

/**
 * Fixed rate refill strategy implementation.
 */
public class FixedRateRefillStrategy implements RefillStrategy {

    private static final long NANOS_IN_SECOND = TimeUnit.SECONDS.toNanos(1);

    private final long tokensPerSecond;
    private final long refillPeriodInNanos;

    private long lastRefillTimeInNanos;
    private long nextRefillTimeInNanos;

    /**
     * Creates fixed rate refill strategy with specified token rate per second
     * and default refill period of 1 millisecond.
     *
     * @param tokensPerSecond Number of tokens to add to the bucket every second.
     */
    public FixedRateRefillStrategy(long tokensPerSecond) {
        this(tokensPerSecond, TimeUnit.MILLISECONDS.toNanos(1));
    }

    /**
     * Creates fixed rate refill strategy with specified token rate per second
     * and specified refill period.
     *
     * @param tokensPerSecond Number of tokens to add to the bucket every second.
     * @param refillPeriodInNanos number of nanoseconds at which refill will be calculated.
     */
    public FixedRateRefillStrategy(long tokensPerSecond, long refillPeriodInNanos) {
        this.tokensPerSecond = tokensPerSecond;
        this.refillPeriodInNanos = refillPeriodInNanos;
        this.lastRefillTimeInNanos = nanoTime();
        this.nextRefillTimeInNanos = lastRefillTimeInNanos + refillPeriodInNanos;
    }

    @Override
    public synchronized long refill() {
        long nowInNanos = nanoTime();
        if (nowInNanos < nextRefillTimeInNanos) {
            return 0;
        }

        // refill every millisecond
        // if more than one millisecond passed
        // refill for the amount of milliseconds passed
        long passedTimeInNanos = nowInNanos - lastRefillTimeInNanos;
        long numOfObtainedTokens = getNumOfTokens(passedTimeInNanos);

        // increment last refilled time just by amount of tokens obtained, rest will be used in next iteration
        // this is to minimize deviation
        lastRefillTimeInNanos += getSpentTimeInNanos(numOfObtainedTokens);
        nextRefillTimeInNanos = lastRefillTimeInNanos + refillPeriodInNanos;
        return limitNumOfTokens(numOfObtainedTokens);
    }

    private long getNumOfTokens(long passedTimeInNanos) {
        return (passedTimeInNanos * tokensPerSecond) / NANOS_IN_SECOND;
    }

    private long getSpentTimeInNanos(long numOfTokens) {
        return (numOfTokens * NANOS_IN_SECOND) / tokensPerSecond;
    }

    private long limitNumOfTokens(long numOfTokens) {
        return numOfTokens > tokensPerSecond ? tokensPerSecond : numOfTokens;
    }

    private long nanoTime() {
        return System.nanoTime();
    }
}
