package io.smartcat.ranger;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.rules.DiscreteRule;
import io.smartcat.ranger.rules.DiscreteRuleBoolean;
import io.smartcat.ranger.rules.ObjectGeneratorRule;
import io.smartcat.ranger.rules.RangeRuleDate;
import io.smartcat.ranger.rules.RangeRuleDouble;
import io.smartcat.ranger.rules.RangeRuleFloat;
import io.smartcat.ranger.rules.RangeRuleInt;
import io.smartcat.ranger.rules.RangeRuleLong;
import io.smartcat.ranger.rules.RangeRuleShort;
import io.smartcat.ranger.rules.Rule;
import io.smartcat.ranger.rules.SubListRule;
import io.smartcat.ranger.rules.SubSetRule;
import io.smartcat.ranger.rules.UUIDRule;

/**
 * Generates objects of certain type and specified criteria.
 *
 * @param <T> Type of object which will be generated.
 */
public class ObjectGenerator<T> implements Iterable<T> {

    private final Class<T> objectType;
    private final int numberOfObjects;
    private final Map<String, Rule<?>> fieldRules;

    private ObjectGenerator(Builder<T> builder) {
        this.objectType = builder.objectType;
        this.numberOfObjects = builder.numberOfObjects;
        this.fieldRules = new HashMap<>(builder.fieldRules);
    }

    /**
     * Generates set <code>numberOfObjects</code> of entities of type {@code <T>}.
     *
     * @return List containing generated entities, or empty list, never null.
     */
    public List<T> generateAll() {
        return generate(numberOfObjects);
    }

    /**
     * Generates passed <code>numberOfObjects</code> of entities of type {@code <T>}.
     *
     * @param numberOfObjects Number of entities to be generated.
     * @return List containing generated entities, or empty list, never null.
     */
    public List<T> generate(int numberOfObjects) {
        List<T> result = new ArrayList<>();
        for (int i = 0; i < numberOfObjects; i++) {
            final T randomEntity = buildOne();
            result.add(randomEntity);
        }

        return result;
    }

    /**
     * Returns total number of objects that should be generated.
     *
     * @return Total number of objects that should be generated.
     */
    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < numberOfObjects;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T result = buildOne();
                currentIndex++;
                return result;
            }
        };
    }

    private T buildOne() {
        final T instance = initObject();
        fieldRules.forEach((key, value) -> set(instance, key, value.next()));
        return instance;
    }

    private T initObject() {
        T instance;
        try {
            instance = objectType.newInstance();
            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the passed value {@code fieldValue} to field {@code fieldName} on the object {@code object}.
     *
     * @param object which field should be set with value
     * @param fieldName name of the field that should be set with value
     * @param fieldValue the value which should be set to field with {@code fieldName}
     * @return true if value is successfully set. Otherwise, false.
     */
    private void set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(object, fieldValue);
                return;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        throw new IllegalArgumentException("Unexisting field: " + fieldName);
    }

    /**
     * Builder for {@link ObjectGenerator}.
     *
     * @param <T> Type which {@link ObjectGenerator} will generate.
     */
    public static class Builder<T> {

        private Class<T> objectType;
        private Map<String, Rule<?>> fieldRules = new HashMap<>();
        private int numberOfObjects = -1;

        /**
         * Creates Builder that is used with passed {@code objectType}.
         *
         * @param objectType Type which {@link ObjectGenerator} will generate.
         */
        public Builder(Class<T> objectType) {
            this.objectType = objectType;
        }

        /**
         * Sets the rule to be used for generating values for field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rule Rule to be used for generating values.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        public <V> Builder<T> withRule(String fieldName, Rule<V> rule) {
            fieldRules.put(fieldName, rule);
            return this;
        }

        /**
         * Sets the boolean rule to be used for generating values for field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @return This builder.
         */
        public Builder<T> withBoolean(String fieldName) {
            Rule<Boolean> rule = new DiscreteRuleBoolean();
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible values for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values List of possible values.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <V> Builder<T> withValues(String fieldName, V... values) {
            return withValues(fieldName, Arrays.asList(values));
        }

        /**
         * Sets the possible values for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values List of possible values.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <V> Builder<T> withValues(String fieldName, Distribution distribution, V... values) {
            return withValues(fieldName, distribution, Arrays.asList(values));
        }

        /**
         * Sets the possible values for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values List of possible values.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        public <V> Builder<T> withValues(String fieldName, List<V> values) {
            Rule<V> rule = new DiscreteRule<>(values);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible values for the field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values List of possible values.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        public <V> Builder<T> withValues(String fieldName, Distribution distribution, List<V> values) {
            Rule<V> rule = new DiscreteRule<>(values, distribution);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the allowed ranges of {@code <R>} for the field with {@code fieldName}. The ranges are defined by an
         * array of {@code <R>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
         * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers Array of {@code <R>} that denotes the ranges.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is empty or not strictly increasing array.
         */
        @SuppressWarnings("unchecked")
        public <R extends Comparable<R>> Builder<T> withRanges(String fieldName, R... rangeMarkers) {
            return withRanges(fieldName, Arrays.asList(rangeMarkers));
        }

        /**
         * Sets the allowed ranges of {@code <R>} for the field with {@code fieldName}. The ranges are defined by an
         * array of {@code <R>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
         * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param rangeMarkers Array of {@code <R>} that denotes the ranges.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is empty or not strictly increasing array.
         */
        @SuppressWarnings("unchecked")
        public <R extends Comparable<R>> Builder<T> withRanges(String fieldName, Distribution distribution,
                R... rangeMarkers) {
            return withRanges(fieldName, distribution, Arrays.asList(rangeMarkers));
        }

        /**
         * Sets the allowed ranges of {@code <R>} for the field with {@code fieldName}. The ranges are defined by an
         * list of {@code <R>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
         * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param rangeMarkers List of {@code <R>} that denotes the ranges.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is null or empty or not strictly increasing list.
         */
        @SuppressWarnings("unchecked")
        public <R extends Comparable<R>> Builder<T> withRanges(String fieldName, List<R> rangeMarkers) {
            Rule<R> rule = createRangeRule(rangeMarkers);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the allowed ranges of {@code <R>} for the field with {@code fieldName}. The ranges are defined by an
         * list of {@code <R>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
         * <code>(n % 2) == 0</code>;
         *
         * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
         * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
         * denotes exclusive end of the range.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param rangeMarkers List of {@code <R>} that denotes the ranges.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         *
         * @throws IllegalArgumentException if {@code rangeMarkers} is null or empty or not strictly increasing list.
         */
        @SuppressWarnings("unchecked")
        public <R extends Comparable<R>> Builder<T> withRanges(String fieldName, Distribution distribution,
                List<R> rangeMarkers) {
            Rule<R> rule = createRangeRule(distribution, rangeMarkers);
            return withRule(fieldName, rule);
        }

        /**
         * Sets object generator to be used for generating values for field with {@code fieldName}.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param objectGenerator Object Generator to be used.
         * @param <V> Type of value which rule will be generate.
         * @return This builder.
         */
        public <V> Builder<T> withObjectGenerator(String fieldName, ObjectGenerator<V> objectGenerator) {
            Rule<V> rule = new ObjectGeneratorRule<>(objectGenerator);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a sub list should be chosen
         * (including empty list).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <R> Builder<T> withSubList(String fieldName, R... values) {
            return withSubList(fieldName, Arrays.asList(values));
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a sub list should be chosen
         * (including empty list).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        public <R> Builder<T> withSubList(String fieldName, List<R> values) {
            Rule<List<R>> rule = new SubListRule<>(values);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a sub list should be chosen
         * (including empty list).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <R> Builder<T> withSubList(String fieldName, Distribution distribution, R... values) {
            return withSubList(fieldName, distribution, Arrays.asList(values));
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a sub list should be chosen
         * (including empty list).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        public <R> Builder<T> withSubList(String fieldName, Distribution distribution, List<R> values) {
            Rule<List<R>> rule = new SubListRule<>(values, distribution);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a random sub set should be
         * chosen (including empty set).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <R> Builder<T> withSubSet(String fieldName, R... values) {
            return withSubSet(fieldName, new HashSet<>(Arrays.asList(values)));
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a random sub set should be
         * chosen (including empty set).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        public <R> Builder<T> withSubSet(String fieldName, Set<R> values) {
            Rule<Set<R>> rule = new SubSetRule<>(values);
            return withRule(fieldName, rule);
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a random sub set should be
         * chosen (including empty set).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        @SuppressWarnings("unchecked")
        public <R> Builder<T> withSubSet(String fieldName, Distribution distribution, R... values) {
            return withSubSet(fieldName, distribution, new HashSet<>(Arrays.asList(values)));
        }

        /**
         * Sets the possible list of values for the field with {@code fieldName} from which a random sub set should be
         * chosen (including empty set).
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @param distribution Distribution to be used when generating values.
         * @param values Possible values.
         * @param <R> Type of value which rule will be generate.
         * @return This builder.
         */
        public <R> Builder<T> withSubSet(String fieldName, Distribution distribution, Set<R> values) {
            Rule<Set<R>> rule = new SubSetRule<>(values, distribution);
            return withRule(fieldName, rule);
        }

        /**
         * Declares that field with passed {@code fieldName} is UUID string.
         *
         * @param fieldName Name of the field in the type {@code <T>}.
         * @return This builder.
         */
        public Builder<T> withUUID(String fieldName) {
            Rule<String> rule = new UUIDRule();
            return withRule(fieldName, rule);
        }

        /**
         * Sets the number of entities of type {@code <T>} to be generated.
         *
         * @param numberOfObjects Number of objects to be generated.
         * @return This builder.
         */
        public Builder<T> toBeGenerated(int numberOfObjects) {
            this.numberOfObjects = numberOfObjects;
            return this;
        }

        /**
         * Builds {@link ObjectGenerator} based on current builder configuration.
         *
         * @return Instance of {@link ObjectGenerator}.
         */
        public ObjectGenerator<T> build() {
            return new ObjectGenerator<>(this);
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Rule createRangeRule(List ranges) {
            if (ranges.isEmpty()) {
                throw new IllegalArgumentException("Ranges cannot be empty");
            }
            Object item = ranges.get(0);
            if (item instanceof Date) {
                return new RangeRuleDate(ranges);
            }
            if (item instanceof LocalDateTime) {
                List<Date> dates = ((List<LocalDateTime>) ranges).stream()
                        .map(marker -> marker.toInstant(ZoneOffset.UTC)).map(instant -> Date.from(instant))
                        .collect(Collectors.toList());
                return new RangeRuleDate(dates);
            }
            if (item instanceof Double) {
                return new RangeRuleDouble(ranges);
            }
            if (item instanceof Float) {
                return new RangeRuleFloat(ranges);
            }
            if (item instanceof Long) {
                return new RangeRuleLong(ranges);
            }
            if (item instanceof Integer) {
                return new RangeRuleInt(ranges);
            }
            if (item instanceof Short) {
                return new RangeRuleShort(ranges);
            }
            Class clazz = item.getClass();
            throw new IllegalArgumentException(
                    "Class: " + clazz.getName() + " is not supported to be used for ranges.");
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private Rule createRangeRule(Distribution distribution, List ranges) {
            if (ranges.isEmpty()) {
                throw new IllegalArgumentException("Ranges cannot be empty");
            }
            Object item = ranges.get(0);
            if (item instanceof Date) {
                return new RangeRuleDate(ranges, distribution);
            }
            if (item instanceof LocalDateTime) {
                List<Date> dates = ((List<LocalDateTime>) ranges).stream()
                        .map(marker -> marker.toInstant(ZoneOffset.UTC)).map(instant -> Date.from(instant))
                        .collect(Collectors.toList());
                return new RangeRuleDate(dates, distribution);
            }
            if (item instanceof Double) {
                return new RangeRuleDouble(ranges, distribution);
            }
            if (item instanceof Float) {
                return new RangeRuleFloat(ranges, distribution);
            }
            if (item instanceof Long) {
                return new RangeRuleLong(ranges, distribution);
            }
            if (item instanceof Integer) {
                return new RangeRuleInt(ranges, distribution);
            }
            if (item instanceof Short) {
                return new RangeRuleShort(ranges, distribution);
            }
            Class clazz = item.getClass();
            throw new IllegalArgumentException(
                    "Class: " + clazz.getName() + " is not supported to be used for ranges.");
        }
    }
}
