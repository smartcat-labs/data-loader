package io.smartcat.data.loader.worker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.Worker;

/**
 * Worker that does nothing. It will just swallow every data it accepts.
 *
 * @param <T> Type of data to accept.
 */
public class NullWorker<T> implements Worker<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NullWorker.class);

    @Override
    public void accept(T data) {
        LOGGER.trace("NullWorker accept method invoked.");
    }
}
