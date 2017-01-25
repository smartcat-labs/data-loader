package io.smartcat.data.loader.api;

/**
 * DataSource interface providing API for load generator data queue.
 *
 * @param <T> Type of the data provided by data source implementation.
 */
public interface DataSource<T> {

    /**
     * Check if data source has more data.
     *
     * @return has next
     */
    boolean hasNext();

    /**
     * Get next value from data source.
     *
     * @return typed value from data source.
     */
    T next();

}
