package io.smartcat.data.loader;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

/**
 * Class used for building random objects of certain type.
 *
 * @param <T>
 */
public class RandomBuilder<T> {

    private Class<T> objectType;

    private Map<String, Rule<?>> fieldRules = new HashMap<>();

    private int numberOfObjects;

    private Map<String, RandomBuilder<?>> nestedObjectBuilderMap = new HashMap<>();

    private Randomizer random = new RandomizerImpl();

    public RandomBuilder(Class<T> objectType) {
        this.objectType = objectType;
    }

    public RandomBuilder(Class<T> objectType, Randomizer random) {
        this.objectType = objectType;
        this.random = random;
    }

    public RandomBuilder<T> randomFromRange(String fieldName, LocalDateTime startDate, LocalDateTime endDate) {
        Instant lower = startDate.toInstant(ZoneOffset.UTC);
        Instant upper = endDate.toInstant(ZoneOffset.UTC);

        fieldRules.put(fieldName, RangeRuleDate.withRanges(Date.from(lower), Date.from(upper)));

        return this;
    }

    public RandomBuilder<T> exclusiveRandomFromRange(String fieldName, LocalDateTime startDate, LocalDateTime endDate) {
        Instant lower = startDate.toInstant(ZoneOffset.UTC);
        Instant upper = endDate.toInstant(ZoneOffset.UTC);

        fieldRules.put(fieldName, RangeRuleDate.withRangesX(Date.from(lower), Date.from(upper)));

        return this;
    }

    public RandomBuilder<T> randomFromRange(String fieldName, Long lower, Long upper) {
        fieldRules.put(fieldName, RangeRule.withRanges(lower, upper));
        return this;
    }

    public RandomBuilder<T> exclusiveRandomFromRange(String fieldName, Long lower, Long upper) {
        fieldRules.put(fieldName, RangeRule.withRangesX(lower, upper));
        return this;
    }

    public RandomBuilder<T> randomFromRange(String fieldName, Double lower, Double upper) {
        fieldRules.put(fieldName, RangeRuleDouble.withRanges(lower, upper));
        return this;
    }

    public RandomBuilder<T> exclusiveRandomFromRange(String fieldName, Double lower, Double upper) {
        fieldRules.put(fieldName, RangeRuleDouble.withRangesX(lower, upper));
        return this;
    }

    public RandomBuilder<T> randomFrom(String fieldName, String... values) {
        fieldRules.put(fieldName, DiscreteRule.newSet(values).withRandom(random));
        return this;
    }

    public RandomBuilder<T> randomWithBuilder(String fieldName, RandomBuilder<?> builder) {
        nestedObjectBuilderMap.put(fieldName, builder);
        return this;
    }

    public RandomBuilder<T> exclusiveRandomFrom(String fieldName, String... values) {
        fieldRules.put(fieldName, DiscreteRule.newSetExclusive(values));
        return this;
    }

    public RandomBuilder<T> randomSubsetFrom(String fieldName, String... values) {
        fieldRules.put(fieldName, SubSetRule.withValues(Arrays.asList(values)));
        return this;
    }

    public RandomBuilder<T> randomSubListFrom(String fieldName, String... values) {
        fieldRules.put(fieldName, SubListRule.withValues(Arrays.asList(values)));
        return this;
    }

    public RandomBuilder<T> toBeBuilt(int numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
        return this;
    }

    public List<T> build(long numberOfEntitiesToBuild) {
        List<T> result = new ArrayList<>();
        for (long i = 1; i <= numberOfEntitiesToBuild; i++) {
            final T randomEntity = buildOne();
            result.add(randomEntity);
        }

        return result;
    }

    private T buildOne() {
        final T instance = initObject();

        fieldRules.keySet().forEach(key -> set(instance, key, fieldRules.get(key).getRandomAllowedValue()));

        nestedObjectBuilderMap.keySet().forEach(key -> set(instance, key, nestedObjectBuilderMap.get(key).buildOne()));

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

    public List<T> buildAll() {
        return this.build(numberOfObjects);
    }

    public Map<String, Rule<?>> getFieldRules() {
        return fieldRules;
    }

    public Map<String, RandomBuilder<?>> getNestedObjectBuilderMap() {
        return nestedObjectBuilderMap;
    }

    public void setNestedObjectBuilderMap(Map<String, RandomBuilder<?>> nestedObjectBuilderMap) {
        this.nestedObjectBuilderMap = nestedObjectBuilderMap;
    }

    public static boolean set(Object object, String fieldName, Object fieldValue) {
        Class<?> clazz = object.getClass();
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);

                field.setAccessible(true);
                field.set(object, fieldValue);
                return true;
            } catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return false;
    }

}
