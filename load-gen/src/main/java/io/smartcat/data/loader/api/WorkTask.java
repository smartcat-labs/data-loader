package io.smartcat.data.loader.api;

/**
 * WorkTask interface providing API for load generator task execution.
 *
 * @param <T> execution parameter. Usually data from provided data source.
 */
public interface WorkTask<T> {

    /**
     * Execute method.
     *
     * @param parameter data parameter
     */
    void execute(T parameter);

}
