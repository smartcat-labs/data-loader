package io.smartcat.data.loader.tokenbuket;

import java.util.concurrent.TimeUnit;

/**
 * Fixed rate refill strategy implementation.
 */
public class FixedRateRefillStrategy implements io.smartcat.data.loader.tokenbuket.RefillStrategy {

    private final Ticker ticker;
    private final long numTokensPerPeriod;
    private final long periodDurationInNanos;
    private long lastRefillTime;
    private long nextRefillTime;

    /**
     * Constructor.
     *
     * @param numTokensPerPeriod The number of tokens to add to the bucket every period.
     * @param period             How often to refill the bucket.
     * @param unit               Unit for period.
     */
    public FixedRateRefillStrategy(long numTokensPerPeriod, long period, TimeUnit unit)
    {
        this.ticker = new Ticker();
        this.numTokensPerPeriod = numTokensPerPeriod;
        this.periodDurationInNanos = unit.toNanos(period);
        this.lastRefillTime = 0;
        this.nextRefillTime = 0;
    }

    @Override
    public synchronized long refill()
    {
        long now = ticker.read();
        if (now < nextRefillTime) {
            return 0;
        }

        lastRefillTime = now;
        nextRefillTime = lastRefillTime + periodDurationInNanos;
        return numTokensPerPeriod;
    }

    private class Ticker {

        public long read() {
            return System.nanoTime();
        }

    }
}
