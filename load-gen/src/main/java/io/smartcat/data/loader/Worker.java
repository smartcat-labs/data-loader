package io.smartcat.data.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smartcat.data.loader.api.WorkTask;

public final class Worker<T> implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

    private WorkTask workTask;
    private T parameter;

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
