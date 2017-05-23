package io.smartcat.ranger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Aggregates multiple {@link ObjectGenerator}s.
 *
 * @param <T> Type of object which will be generated.
 */
public class AggregatedObjectGenerator<T> implements Iterable<T> {

    private final List<ObjectGenerator<T>> objectGenerators;

    private AggregatedObjectGenerator(Builder<T> builder) {
        this.objectGenerators = new ArrayList<>(builder.objectGenerators);
    }

    /**
     * Generates entities of type {@code <T>} from all set object generators.
     *
     * @return List containing generated entities, or empty list, never null.
     */
    public List<T> generateAll() {
        List<T> result = new ArrayList<T>();
        for (T object : this) {
            result.add(object);
        }
        return result;
    }

    @Override
    public Iterator<T> iterator() {
        // TODO Instead of appending results from each object generator at the end
        // use weighted distribution based on number of objects in each object generator
        final int totalNumberOfObjects = objectGenerators.stream().mapToInt(x -> x.getNumberOfObjects()).sum();
        return new Iterator<T>() {

            int currentGeneratorIndex = 0;
            Iterator<T> currentIterator;
            int elementsFetched = 0;

            @Override
            public boolean hasNext() {
                return elementsFetched < totalNumberOfObjects;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                if (currentIterator == null) {
                    currentIterator = objectGenerators.get(currentGeneratorIndex).iterator();
                }
                // if IndexOutOfBoundsException is thrown then it is a bug
                // since totalNumberOfObjects and real number of objects must match
                while (!currentIterator.hasNext()) {
                    currentGeneratorIndex++;
                    currentIterator = objectGenerators.get(currentGeneratorIndex).iterator();
                }
                T result = currentIterator.next();
                elementsFetched++;
                return result;
            }
        };
    }

    /**
     * Builder for {@link AggregatedObjectGenerator}.
     *
     * @param <T> Type which {@link AggregatedObjectGenerator} will generate.
     */
    public static class Builder<T> {

        private final List<ObjectGenerator<T>> objectGenerators = new ArrayList<>();

        /**
         * Add generator to the list.
         *
         * @param objectGenerator Typed object generator.
         * @return This builder.
         */
        public Builder<T> withObjectGenerator(ObjectGenerator<T> objectGenerator) {
            objectGenerators.add(objectGenerator);
            return this;
        }

        /**
         * Builds {@link AggregatedObjectGenerator} based on current builder configuration.
         *
         * @return Instance of {@link AggregatedObjectGenerator}.
         */
        public AggregatedObjectGenerator<T> build() {
            return new AggregatedObjectGenerator<>(this);
        }
    }
}
