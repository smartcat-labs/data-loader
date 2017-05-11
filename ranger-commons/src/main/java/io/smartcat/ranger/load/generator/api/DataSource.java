package io.smartcat.ranger.load.generator.api;

/**
 * Interface providing API for source of data.
 *
 * @param <T> Type of the data provided by data source implementation.
 */
public interface DataSource<T> {

    /**
     * Returns true if data source can provide next value, otherwise false.
     *
     * @param time Relative time in nanoseconds from starting load generator. Time can be used for implementing time
     *            dependent data source which can then return data with time stamps and/or data influenced by time in
     *            any other way.
     * @return True if data source can provide next value, otherwise false.
     */
    boolean hasNext(long time);

    /**
     * Returns next value from this data source.
     *
     * @param time Relative time in nanoseconds from starting load generator. Time can be used for implementing time
     *            dependent data source which can then return data with time stamps and/or data influenced by time in
     *            any other way.
     * @return Next value from this data source.
     */
    T getNext(long time);
}
