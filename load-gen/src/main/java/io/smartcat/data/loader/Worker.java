package io.smartcat.data.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.WorkTask;

/**
 * Worker runnable executing work tasks.
 *
 * @param <T> work task parameter type
 */
public final class Worker<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private WorkTask<T> workTask;
    private T parameter;

    /**
     * Constructor.
     *
     * @param workTask  function to be executed
     * @param parameter function parameter
     */
    public Worker(WorkTask<T> workTask, T parameter) {
        this.workTask = workTask;
        this.parameter = parameter;
    }

    @Override
    public void run() {
        try {
            workTask.accept(parameter);
        } catch (Exception e) {
            LOGGER.error("Exception wile executing a callable function", e);
        }
    }
}
