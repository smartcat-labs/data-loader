package io.smartcat.ranger.load.generator.datasource;

import java.util.Iterator;
import java.util.SplittableRandom;

import io.smartcat.ranger.load.generator.api.DataSource;

/**
 * Endless data source generating random <code>Integer</code> values.
 */
public class RandomIntDataSource implements DataSource<Integer> {

    private final Iterator<Integer> it = new SplittableRandom().ints().iterator();

    @Override
    public boolean hasNext(long time) {
        return true;
    }

    @Override
    public Integer getNext(long time) {
        return it.next();
    }
}
