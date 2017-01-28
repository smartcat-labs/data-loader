package io.smartcat.data.loader.tokenbuket;

public class TokenBucket {

    private final long capacity;
    private final SleepStrategy sleepStrategy;
    private final RefillStrategy refillStrategy;

    private long size;

    public TokenBucket(long capacity, long initialTokens, RefillStrategy refillStrategy, SleepStrategy sleepStrategy) {

        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity must be positive");
        }

        if (initialTokens > capacity) {
            throw new IllegalArgumentException("initialTokens must be equal or less than capacity");
        }

        this.capacity = capacity;
        this.size = initialTokens;
        this.refillStrategy = refillStrategy;
        this.sleepStrategy = sleepStrategy;
    }

    public void get() {
        get(1);
    }

    public void get(long tokens) {
        while (true) {
            if (tryGet(tokens)) {
                break;
            }

            sleepStrategy.sleep();
        }
    }

    public synchronized boolean tryGet(long tokens) {
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
        size = Math.max(0, Math.min(size + Math.abs(tokens), capacity));
    }

}
