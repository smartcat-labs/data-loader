package io.smartcat.data.loader.api;

import java.util.Iterator;

/**
 * DataSource interface providing API for load generator data queue.
 *
 * @param <Object> Type of the data provided by data source implementation.
 */
public interface DataSource<Object> extends Iterator<Object> {

}
