package io.smartcat.data.loader.util;

import io.smartcat.data.loader.api.WorkTask;

/**
 * NoOp work task implementation.
 */
public class NoOpWorkTask implements WorkTask<Integer> {
    @Override
    public void execute(Integer parameter) {

    }
}
