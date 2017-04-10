package io.smartcat.data.loader.util;

import io.smartcat.data.loader.api.DataSource;

import java.util.Iterator;
import java.util.SplittableRandom;

/**
 * Endless {@link DataSource} providing random int values.
 */
public class RandomIntDataSource implements DataSource<Integer> {

    private final Iterator<Integer> iterator;

    /**
     * Constructor.
     */
    public RandomIntDataSource() {
        this.iterator = new SplittableRandom().ints().iterator();
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Integer next() {
        return iterator.next();
    }
}
