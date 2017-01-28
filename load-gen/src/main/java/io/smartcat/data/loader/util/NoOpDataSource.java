package io.smartcat.data.loader.util;

import java.util.Random;

import io.smartcat.data.loader.api.DataSource;

/**
 * NoOp data source implementation. Returns random integers as data.
 */
public class NoOpDataSource implements DataSource<Integer> {

    private Random random = new Random();

    @Override
    public boolean hasNext() {
        return true;
    }

    @Override
    public Integer next() {
        return random.nextInt();
    }
}
