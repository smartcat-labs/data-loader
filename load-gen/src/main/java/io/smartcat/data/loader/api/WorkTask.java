package io.smartcat.data.loader.api;

import java.util.function.Consumer;

/**
 * WorkTask interface providing API for load generator task execution.
 *
 * @param <T> Type being passed to the worker.
 */
public interface WorkTask<T> extends Consumer<T> {

}
