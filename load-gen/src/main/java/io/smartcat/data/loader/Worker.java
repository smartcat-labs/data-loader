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

    private WorkTask workTask;
    private T parameter;

    /**
     * Constructor.
     *
     * @param workTask  work task to be executed
     * @param parameter work task parameter
     */
    public Worker(WorkTask workTask, T parameter) {
        this.workTask = workTask;
        this.parameter = parameter;
    }

    @Override
    public void run() {
        try {
            workTask.execute(parameter);
        } catch (Exception e) {
            LOGGER.error("Exception wile executing a callable function", e);
        }
    }
}
