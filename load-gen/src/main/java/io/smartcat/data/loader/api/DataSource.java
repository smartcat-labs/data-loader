package io.smartcat.data.loader.api;

public interface DataSource<T> {

    boolean hasNext();

    T next();

}
