package io.smartcat.data.loader;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Worker runnable executing work tasks.
 *
 * @param <T> work task parameter type
 */
public final class Worker<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private Consumer<T> consumer;
    private T parameter;

    /**
     * Constructor.
     *
     * @param consumer  function to be executed
     * @param parameter function parameter
     */
    public Worker(Consumer<T> consumer, T parameter) {
        this.consumer = consumer;
        this.parameter = parameter;
    }

    @Override
    public void run() {
        try {
            consumer.accept(parameter);
        } catch (Exception e) {
            LOGGER.error("Exception wile executing a callable function", e);
        }
    }
}
