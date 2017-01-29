package io.smartcat.data.loader.tokenbuket;

/**
 * Encapsulation of a refilling strategy for a token bucket.
 */
public interface RefillStrategy {
    /**
     * Returns the number of tokens to add to the token bucket.
     *
     * @return The number of tokens to add to the token bucket.
     */
    long refill();
}
