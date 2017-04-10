package io.smartcat.data.loader.datasource;

import java.util.Iterator;
import java.util.SplittableRandom;

import io.smartcat.data.loader.api.DataSource;

/**
 * Endless data source generating random <code>Long</code> values.
 */
public class RandomLongDataSource implements DataSource<Long> {

    private final Iterator<Long> it = new SplittableRandom().longs().iterator();

    @Override
    public boolean hasNext(long time) {
        return true;
    }

    @Override
    public Long getNext(long time) {
        return it.next();
    }
}
