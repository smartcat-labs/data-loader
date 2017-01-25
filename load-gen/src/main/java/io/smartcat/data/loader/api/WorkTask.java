package io.smartcat.data.loader.api;

public interface WorkTask<T> {

    void execute(T parameter);

}
