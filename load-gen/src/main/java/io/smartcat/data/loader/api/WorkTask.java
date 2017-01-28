package io.smartcat.data.loader.api;

import java.util.function.Consumer;

/**
 * WorkTask interface providing API for load generator task execution.
 *
 * @param <T> execution parameter. Usually data from provided data source.
 */
public interface WorkTask<T> extends Consumer<T> {

}
