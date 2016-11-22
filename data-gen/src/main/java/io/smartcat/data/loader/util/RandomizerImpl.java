package io.smartcat.data.loader.util;

import java.util.Random;

/**
 * Wrapper for java.util.Random.
 */
public class RandomizerImpl implements Randomizer {

    private Random random = new Random();

    /**
     * Returns a random int value between 0 (inclusive) and the specified value (exclusive).
     * @param bound
     * @return random int
     */
    @Override
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

}
