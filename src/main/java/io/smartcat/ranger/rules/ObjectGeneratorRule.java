package io.smartcat.ranger.rules;

import io.smartcat.ranger.ObjectGenerator;

/**
 * Rule that uses {@link ObjectGenerator} for generating values.
 *
 * @param <T> Type of value which will be generated.
 */
public class ObjectGeneratorRule<T> implements Rule<T> {

    private final ObjectGenerator<T> objectGenerator;

    /**
     * Constructs rule with specified <code>objectGenerator</code>.
     *
     * @param objectGenerator Object generator which will be used for value generation.
     */
    public ObjectGeneratorRule(ObjectGenerator<T> objectGenerator) {
        this.objectGenerator = objectGenerator;
    }

    @Override
    public T next() {
        return objectGenerator.iterator().next();
    }
}
