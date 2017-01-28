package io.smartcat.data.loader.api;

import java.util.Iterator;

/**
 * DataSource interface providing API for load generator data queue.
 *
 * @param <T> Type of the data provided by data source implementation.
 */
public interface DataSource<T> extends Iterator<T> {

}
