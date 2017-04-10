package io.smartcat.data.loader.util;

import io.smartcat.data.loader.api.WorkTask;

/**
 * NoOp work task implementation. It will just swallow every data it accepts.
 *
 * @param <T> Type of data to accept.
 */
public class NoOpWorkTask<T> implements WorkTask<T> {

    @Override
    public void accept(T integer) {

    }

}
