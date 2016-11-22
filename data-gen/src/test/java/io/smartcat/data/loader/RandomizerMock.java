package io.smartcat.data.loader;

import io.smartcat.data.loader.util.Randomizer;

/**
 * Mock class to be used in tests.
 */
public class RandomizerMock implements Randomizer {

    private int seed = 0;

    @Override
    public int nextInt(int bound) {
        return seed++;
    }

}
