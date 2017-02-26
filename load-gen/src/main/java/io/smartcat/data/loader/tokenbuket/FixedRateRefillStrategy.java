package io.smartcat.data.loader.tokenbuket;

import java.util.concurrent.TimeUnit;

/**
 * Fixed rate refill strategy implementation.
 */
public class FixedRateRefillStrategy implements RefillStrategy {

    private final Ticker ticker;
    private final long numTokensPerSecond;
    private final long periodDurationInNanos;
    private long lastRefillTime;
    private long nextRefillTime;

    /**
     * Constructor.
     *
     * @param numTokensPerSecond The number of tokens to add to the bucket every second.
     */
    public FixedRateRefillStrategy(long numTokensPerSecond) {
        this.ticker = new Ticker();
        this.numTokensPerSecond = numTokensPerSecond;
        this.periodDurationInNanos = TimeUnit.SECONDS.toNanos(1);
        this.lastRefillTime = 0;
        this.nextRefillTime = 0;
    }

    @Override
    public synchronized long refill() {
        long now = ticker.read();
        if (now < nextRefillTime) {
            return 0;
        }

        lastRefillTime = now;
        nextRefillTime = lastRefillTime + periodDurationInNanos;
        return numTokensPerSecond;
    }

    /**
     * Ticker class.
     */
    private class Ticker {

        public long read() {
            return System.nanoTime();
        }

    }
}
