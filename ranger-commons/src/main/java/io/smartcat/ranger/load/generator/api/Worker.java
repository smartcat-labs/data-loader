package io.smartcat.ranger.load.generator.api;

import java.util.function.Consumer;

/**
 * Worker interface providing API for task execution.
 *
 * @param <T> Type of data workerConfiguration accepts.
 */
public interface Worker<T> extends Consumer<T> {

}
