package io.smartcat.data.loader.api;

import java.util.function.Consumer;

/**
 * Worker interface providing API for {@link LoadGenerator} task execution.
 *
 * @param <T> Type of data worker accepts.
 */
public interface Worker<T> extends Consumer<T> {

}
