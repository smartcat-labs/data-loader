package io.smartcat.data.loader.tokenbuket;

/**
 * Token bucket algorithm implementation.
 */
public class TokenBucket {

    private final long capacity = Long.MAX_VALUE;
    private final SleepStrategy sleepStrategy;
    private final RefillStrategy refillStrategy;

    private long size;

    /**
     * Constructor.
     *
     * @param initialTokens number of initial tokens
     * @param refillStrategy refill strategy
     * @param sleepStrategy sleep strategy
     */
    public TokenBucket(long initialTokens, RefillStrategy refillStrategy, SleepStrategy sleepStrategy) {

        this.size = initialTokens;
        this.refillStrategy = refillStrategy;
        this.sleepStrategy = sleepStrategy;
    }

    /**
     * Get one token or block until one token available.
     */
    public void get() {
        get(1);
    }

    /**
     * Get number of tokens or block until that number of tokens is available.
     *
     * @param tokens number of tokens
     */
    public void get(long tokens) {
        while (true) {
            if (tryGet(tokens)) {
                break;
            }

            sleepStrategy.sleep();
        }
    }

    private synchronized boolean tryGet(long tokens) {
        if (tokens <= 0) {
            throw new IllegalArgumentException("Number of tokens to consume must be positive");
        }

        if (tokens > capacity) {
            throw new IllegalArgumentException(
                    "Number of tokens to consume must be less than the capacity of the bucket.");
        }

        refill(refillStrategy.refill());

        if (tokens <= size) {
            size -= tokens;
            return true;
        }

        return false;
    }

    private synchronized void refill(long tokens) {
        size += tokens;
    }

}
