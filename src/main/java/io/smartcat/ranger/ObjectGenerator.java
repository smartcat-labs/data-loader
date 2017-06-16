package io.smartcat.ranger;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.ranger.core.Value;

/**
 * Generates objects of type {@code <T>}.
 *
 * @param <T> Type of objects to be generated.
 */
public class ObjectGenerator<T> {

    final Value<T> value;

    /**
     * Constructs object generator out of specified <code>value</code>.
     *
     * @param value The value.
     */
    public ObjectGenerator(Value<T> value) {
        this.value = value;
    }

    /**
     * Generates list containing specified <code>numberOfObjects</code>.
     *
     * @param numberOfObjects Number of objects to be generated.
     * @return List of generated objects, or empty list, never null.
     */
    public List<T> generate(int numberOfObjects) {
        if (numberOfObjects < 0) {
            throw new IllegalArgumentException(
                    "Cannot generate negative number of objects. numberOfObjects: " + numberOfObjects);
        }
        List<T> result = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            final T randomEntity = buildOne();
            result.add(randomEntity);
        }
        return result;
    }

    /**
     * Generates next object.
     *
     * @return An instance of {@code <T>}.
     */
    public T next() {
        return buildOne();
    }

    private T buildOne() {
        T result = value.get();
        value.reset();
        return result;
    }
}
