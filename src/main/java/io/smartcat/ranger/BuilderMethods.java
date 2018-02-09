package io.smartcat.ranger;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import io.smartcat.ranger.core.CircularRangeValueByte;
import io.smartcat.ranger.core.CircularRangeValueDouble;
import io.smartcat.ranger.core.CircularRangeValueFloat;
import io.smartcat.ranger.core.CircularRangeValueInt;
import io.smartcat.ranger.core.CircularRangeValueLong;
import io.smartcat.ranger.core.CircularRangeValueShort;
import io.smartcat.ranger.core.CircularValue;
import io.smartcat.ranger.core.ConstantValue;
import io.smartcat.ranger.core.DiscreteValue;
import io.smartcat.ranger.core.EmptyListValue;
import io.smartcat.ranger.core.EmptyMapValue;
import io.smartcat.ranger.core.ExactWeightedValue;
import io.smartcat.ranger.core.ExactWeightedValue.CountValuePair;
import io.smartcat.ranger.core.GetterTransformer;
import io.smartcat.ranger.core.JsonTransformer;
import io.smartcat.ranger.core.ListValue;
import io.smartcat.ranger.core.NowDateValue;
import io.smartcat.ranger.core.NowLocalDateTimeValue;
import io.smartcat.ranger.core.NowLocalDateValue;
import io.smartcat.ranger.core.NowValue;
import io.smartcat.ranger.core.RandomContentStringValue;
import io.smartcat.ranger.core.RandomLengthListValue;
import io.smartcat.ranger.core.RangeValue;
import io.smartcat.ranger.core.RangeValueByte;
import io.smartcat.ranger.core.RangeValueDate;
import io.smartcat.ranger.core.RangeValueDouble;
import io.smartcat.ranger.core.RangeValueFloat;
import io.smartcat.ranger.core.RangeValueInt;
import io.smartcat.ranger.core.RangeValueLocalDate;
import io.smartcat.ranger.core.RangeValueLocalDateTime;
import io.smartcat.ranger.core.RangeValueLong;
import io.smartcat.ranger.core.RangeValueShort;
import io.smartcat.ranger.core.StringTransformer;
import io.smartcat.ranger.core.TimeFormatTransformer;
import io.smartcat.ranger.core.UUIDValue;
import io.smartcat.ranger.core.Value;
import io.smartcat.ranger.core.WeightedValue;
import io.smartcat.ranger.core.WeightedValue.WeightedValuePair;
import io.smartcat.ranger.core.arithmetic.AdditionValueByte;
import io.smartcat.ranger.core.arithmetic.AdditionValueDouble;
import io.smartcat.ranger.core.arithmetic.AdditionValueFloat;
import io.smartcat.ranger.core.arithmetic.AdditionValueInteger;
import io.smartcat.ranger.core.arithmetic.AdditionValueLong;
import io.smartcat.ranger.core.arithmetic.AdditionValueShort;
import io.smartcat.ranger.core.arithmetic.DivisionValueByte;
import io.smartcat.ranger.core.arithmetic.DivisionValueDouble;
import io.smartcat.ranger.core.arithmetic.DivisionValueFloat;
import io.smartcat.ranger.core.arithmetic.DivisionValueInteger;
import io.smartcat.ranger.core.arithmetic.DivisionValueLong;
import io.smartcat.ranger.core.arithmetic.DivisionValueShort;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueByte;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueDouble;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueFloat;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueInteger;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueLong;
import io.smartcat.ranger.core.arithmetic.MultiplicationValueShort;
import io.smartcat.ranger.core.arithmetic.SubtractionValueByte;
import io.smartcat.ranger.core.arithmetic.SubtractionValueDouble;
import io.smartcat.ranger.core.arithmetic.SubtractionValueFloat;
import io.smartcat.ranger.core.arithmetic.SubtractionValueInteger;
import io.smartcat.ranger.core.arithmetic.SubtractionValueLong;
import io.smartcat.ranger.core.arithmetic.SubtractionValueShort;
import io.smartcat.ranger.core.csv.CsvReaderValue;
import io.smartcat.ranger.core.csv.CSVParserSettings;
import io.smartcat.ranger.distribution.Distribution;

/**
 * Set of helper methods to use with {@link ObjectGeneratorBuilder}.
 */
public class BuilderMethods {

    private BuilderMethods() {
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which always returns specified value.
     *
     * @param value Value to be returned by created constant object generator.
     * @param <T> Type this value would evaluate to.
     *
     * @return An instance of {@link ObjectGenerator}.
     */
    public static <T> ObjectGenerator<T> constant(T value) {
        return wrap(ConstantValue.of(value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Placeholder for value is defined as '{}', first placeholder uses first value, second, second
     * value, and so on.
     *
     * @param format Format string,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> string(String format, Object... values) {
        return string(format, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates a formatted string using the specified format
     * string and objects. Placeholder for value is defined as '{}', first placeholder uses first value, second, second
     * value, and so on.
     *
     * @param format Format string,
     * @param values List of values.
     * @return An instance of {@link ObjectGenerator} which generates formated strings.
     */
    public static ObjectGenerator<String> string(String format, List<Object> values) {
        return wrap(new StringTransformer(format, unwrapRaw(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates UUIDs.
     *
     * @return An instance of {@link ObjectGenerator} which generates UUIDS.
     */
    public static ObjectGenerator<String> uuid() {
        return wrap(new UUIDValue());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> random(T... values) {
        return random(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values from specified list.
     */
    public static <T> ObjectGenerator<T> random(List<T> values) {
        return wrap(new DiscreteValue<>(unwrap(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     * specified list.
     *
     * @param distribution Distribution to use.
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     *         specified list.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> random(Distribution distribution, T... values) {
        return random(distribution, Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     * specified list.
     *
     * @param distribution Distribution to use.
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution from
     *         specified list.
     */
    public static <T> ObjectGenerator<T> random(Distribution distribution, List<T> values) {
        return wrap(new DiscreteValue<>(unwrap(values), distribution));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     * range.
     *
     * @param range The range.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     *         range.
     */
    public static <T> ObjectGenerator<T> random(Range<T> range) {
        return random(range, RangeValue.defaultUseEdgeCases());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     * range and can force generating edge cases first.
     *
     * @param range The range.
     * @param useEdgeCases Determines whether to generate use cases first or not.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates uniformly distributed values within specified
     *         range.
     */
    public static <T> ObjectGenerator<T> random(Range<T> range, boolean useEdgeCases) {
        return random(range, useEdgeCases, RangeValue.defaultDistrbution());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values distributed by specified distribution
     * within specified range and can force generating edge cases first.
     *
     * @param range The range.
     * @param useEdgeCases Determines whether to generate use cases first or not.
     * @param distribution Distribution to use.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values distributed by specified distribution
     *         within specified range.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> random(Range<T> range, boolean useEdgeCases, Distribution distribution) {
        if (range == null) {
            throw new IllegalArgumentException("range cannot be null.");
        }
        if (distribution == null) {
            throw new IllegalArgumentException("distribution cannot be null.");
        }
        if (range.beginning instanceof LocalDate) {
            return (ObjectGenerator<T>) wrap(new RangeValueLocalDate((LocalDate) range.beginning, (LocalDate) range.end,
                    useEdgeCases, distribution));
        }
        if (range.beginning instanceof LocalDateTime) {
            return (ObjectGenerator<T>) wrap(new RangeValueLocalDateTime((LocalDateTime) range.beginning,
                    (LocalDateTime) range.end, useEdgeCases, distribution));
        }
        if (range.beginning instanceof Byte) {
            return (ObjectGenerator<T>) wrap(new RangeValueByte(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Short) {
            return (ObjectGenerator<T>) wrap(new RangeValueShort(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Integer) {
            return (ObjectGenerator<T>) wrap(new RangeValueInt(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Long) {
            return (ObjectGenerator<T>) wrap(new RangeValueLong(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Float) {
            return (ObjectGenerator<T>) wrap(new RangeValueFloat(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Double) {
            return (ObjectGenerator<T>) wrap(new RangeValueDouble(toRange(range), useEdgeCases, distribution));
        }
        if (range.beginning instanceof Date) {
            return (ObjectGenerator<T>) wrap(new RangeValueDate(toRange(range), useEdgeCases, distribution));
        }
        throw new RuntimeException("Type: " + range.beginning.getClass().getName() + " not supported.");
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in sequence within specified range with
     * specified step. When values from the range are depleted, it starts again from the beginning of the range.
     *
     * @param range The range.
     * @param step The step.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in sequence within specified range with
     *         specified step.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> circular(Range<T> range, T step) {
        if (range.beginning instanceof Byte) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueByte(toRange(range), (Byte) step));
        }
        if (range.beginning instanceof Short) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueShort(toRange(range), (Short) step));
        }
        if (range.beginning instanceof Integer) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueInt(toRange(range), (Integer) step));
        }
        if (range.beginning instanceof Long) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueLong(toRange(range), (Long) step));
        }
        if (range.beginning instanceof Float) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueFloat(toRange(range), (Float) step));
        }
        if (range.beginning instanceof Double) {
            return (ObjectGenerator<T>) wrap(new CircularRangeValueDouble(toRange(range), (Double) step));
        }
        throw new RuntimeException("Type: " + range.beginning.getClass().getName() + " not supported.");
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in order they are specified. When values
     * are depleted, it starts again from the beginning of the list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in order they are specified.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> circular(T... values) {
        return circular(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates values in order they are specified. When values
     * are depleted, it starts again from the beginning of the list.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates values in order they are specified.
     */
    public static <T> ObjectGenerator<T> circular(List<T> values) {
        return wrap(new CircularValue<>(unwrap(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates list containing all values specified.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing all values specified.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<List<T>> list(T... values) {
        return list(Arrays.asList(values));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates list containing all values specified.
     *
     * @param values List of values.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing all values specified.
     */
    public static <T> ObjectGenerator<List<T>> list(List<T> values) {
        return wrap(new ListValue<>(unwrap(values)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates empty list.
     *
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates empty list.
     */
    public static <T> ObjectGenerator<List<T>> emptyList() {
        return wrap(new EmptyListValue<>());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates empty map.
     *
     * @param <K> Key type of map which {@link ObjectGenerator} will generate.
     * @param <V> Value type of map which {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates empty map.
     */
    public static <K, V> ObjectGenerator<Map<K, V>> emptyMap() {
        return wrap(new EmptyMapValue<>());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random length list containing all values
     * specified.
     *
     * @param minLength Minimum length for result list.
     * @param maxLength Maximum length for result list.
     * @param elementGenerator Values generator.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing generated elements with size
     *         between minLength and maxLength.
     */
    public static <T> ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator) {
        return wrap(new RandomLengthListValue<>(minLength, maxLength, elementGenerator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random length list containing all values
     * specified.
     *
     * @param minLength Minimum length for result list.
     * @param maxLength Maximum length for result list.
     * @param elementGenerator Values generator.
     * @param distribution Distribution to use for number of items in list.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates list containing generated elements with size
     *         between minLength and maxLength.
     */
    public static <T> ObjectGenerator<List<T>> list(int minLength, int maxLength, ObjectGenerator<T> elementGenerator,
            Distribution distribution) {
        return wrap(new RandomLengthListValue<>(minLength, maxLength, elementGenerator.value, distribution));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain following characters [A-Za-z0-9]. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthGenerator Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthGenerator) {
        return wrap(new RandomContentStringValue(lengthGenerator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain specified character ranges. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param ranges List of ranges from which characters are taken with uniform distribution.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    @SafeVarargs
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue,
            Range<Character>... ranges) {
        return randomContentString(lengthValue, Arrays.asList(ranges));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates random string values of length to which
     * <code>lengthGenerator</code> evaluates to. Each generation can evaluate to different string length, based on
     * value generated by <code>lengthGenerator</code>. String will contain specified character ranges. Uniform
     * distribution is used to select characters from character ranges.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param ranges List of ranges from which characters are taken with uniform distribution.
     * @return An instance of {@link ObjectGenerator} which generates random string values of specified length.
     */
    public static ObjectGenerator<String> randomContentString(ObjectGenerator<Integer> lengthValue,
            List<Range<Character>> ranges) {
        List<io.smartcat.ranger.core.Range<Character>> convertedRanges = new ArrayList<>();
        for (Range<Character> range : ranges) {
            convertedRanges.add(toRange(range));
        }
        return wrap(new RandomContentStringValue(lengthValue.value, convertedRanges));
    }

    /**
     * Creates an instance of {@link Range}. This is a helper method that is useful for following methods:<br>
     * {@link #random(Range)}<br>
     * {@link #random(Range, boolean)}<br>
     * {@link #random(Range, boolean, Distribution)}<br>
     * {@link #circular(Range, Object)}<br>
     * {@link #randomContentString(ObjectGenerator, Range...)}<br>
     * {@link #randomContentString(ObjectGenerator, List)}<br>
     *
     * @param beginning Beginning of the range.
     * @param end End of the range.
     * @param <T> Type of the range.
     * @return An instance of {@link Range}.
     */
    public static <T> Range<T> range(T beginning, T end) {
        return new Range<>(beginning, end);
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates current time in milliseconds.
     *
     * @return An instance of {@link ObjectGenerator} which generates current time in milliseconds.
     */
    public static ObjectGenerator<Long> now() {
        return wrap(new NowValue());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates current date-time as {@link Date} object.
     *
     * @return An instance of {@link ObjectGenerator} which generates current date-time as {@link Date} object.
     */
    public static ObjectGenerator<Date> nowDate() {
        return wrap(new NowDateValue());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates current date as {@link LocalDate} object.
     *
     * @return An instance of {@link ObjectGenerator} which generates current date as {@link LocalDate} object.
     */
    public static ObjectGenerator<LocalDate> nowLocalDate() {
        return wrap(new NowLocalDateValue());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates current date-time as {@link LocalDateTime} object.
     *
     * @return An instance of {@link ObjectGenerator} which generates current date-time as {@link LocalDateTime} object.
     */
    public static ObjectGenerator<LocalDateTime> nowLocalDateTime() {
        return wrap(new NowLocalDateTimeValue());
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which adds up values of given object generators.
     *
     * @param type Type object generator will return.
     * @param summand1 First object generator for addition.
     * @param summand2 Second object generator for addition.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which adds up values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> add(Class<T> type, ObjectGenerator<T> summand1, ObjectGenerator<T> summand2) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueByte(summand1.value, summand2.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueShort(summand1.value, summand2.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueInteger(summand1.value, summand2.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueLong(summand1.value, summand2.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueFloat(summand1.value, summand2.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) wrap(new AdditionValueDouble(summand1.value, summand2.value));
        } else {
            throw new RuntimeException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which subtracts values of given object generators.
     *
     * @param type Type object generator will return.
     * @param minuend Object generator which will be used as minuend.
     * @param subtrahend Object generator which will be used as subtrahend
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which subtracts values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> subtract(Class<T> type, ObjectGenerator<T> minuend,
            ObjectGenerator<T> subtrahend) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueByte(minuend.value, subtrahend.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueShort(minuend.value, subtrahend.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueInteger(minuend.value, subtrahend.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueLong(minuend.value, subtrahend.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueFloat(minuend.value, subtrahend.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) wrap(new SubtractionValueDouble(minuend.value, subtrahend.value));
        } else {
            throw new RuntimeException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which multiplies values of given object generators.
     *
     * @param type Type object generator will return.
     * @param factor1 First object generator for multiplication.
     * @param factor2 Second object generator for multiplication.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which multiplies values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> multiply(Class<T> type, ObjectGenerator<T> factor1,
            ObjectGenerator<T> factor2) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueByte(factor1.value, factor2.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueShort(factor1.value, factor2.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueInteger(factor1.value, factor2.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueLong(factor1.value, factor2.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueFloat(factor1.value, factor2.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) wrap(new MultiplicationValueDouble(factor1.value, factor2.value));
        } else {
            throw new RuntimeException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which divides values of given object generators.
     *
     * @param type Type object generator will return.
     * @param dividend Object generator which will be used as dividend.
     * @param divisor Object generator which will be used as divisor.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which divides values of given object generators.
     */
    @SuppressWarnings("unchecked")
    public static <T> ObjectGenerator<T> divide(Class<T> type, ObjectGenerator<T> dividend,
            ObjectGenerator<T> divisor) {
        if (type.equals(Byte.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueByte(dividend.value, divisor.value));
        } else if (type.equals(Short.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueShort(dividend.value, divisor.value));
        } else if (type.equals(Integer.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueInteger(dividend.value, divisor.value));
        } else if (type.equals(Long.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueLong(dividend.value, divisor.value));
        } else if (type.equals(Float.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueFloat(dividend.value, divisor.value));
        } else if (type.equals(Double.class)) {
            return (ObjectGenerator<T>) wrap(new DivisionValueDouble(dividend.value, divisor.value));
        } else {
            throw new RuntimeException("Type: " + type.getName() + " not supported.");
        }
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted. Default values for other parameters:
     * <ul>
     * <li><code>delimiter</code> - <code>','</code></li>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>null</code> (disabled)</li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * </ul>
     *
     * @param path Path to the CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     */
    public static ObjectGenerator<Map<String, String>> csv(String path) {
        return wrap(new CsvReaderValue(new CSVParserSettings(path)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted. Default values for other parameters:
     * <ul>
     * <li><code>recordSeparator</code> - <code>"\n"</code></li>
     * <li><code>trim</code> - <code>true</code></li>
     * <li><code>quote</code> - <code>null</code> (disabled)</li>
     * <li><code>commentMarker</code> - <code>'#'</code></li>
     * <li><code>ignoreEmptyLines</code> - <code>true</code></li>
     * <li><code>nullString</code> - <code>null</code> (disabled)</li>
     * </ul>
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     */
    public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter) {
        return wrap(new CsvReaderValue(new CSVParserSettings(path, delimiter)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which reads CSV file and returns new record on each iteration
     * until CSV file is depleted.
     *
     * @param path Path to the CSV file.
     * @param delimiter Delimiter of columns within CSV file.
     * @param recordSeparator Delimiter of records within CSV file.
     * @param trim True if each column value is to be trimmed for leading and trailing whitespace, otherwise
     *            <code>false</code>.
     * @param quote Character that will be stripped from beginning and end of each column if present. If set to
     *            <code>null</code>, no characters will be stripped (nothing will be used as quote character).
     * @param commentMarker Character to use as a comment marker, everything after it is considered comment.
     * @param ignoreEmptyLines True if empty lines are to be ignored, otherwise <code>false</code>.
     * @param nullString Converts string with given value to <code>null</code>. If set to <code>null</code>, no
     *            conversion will be done.
     * @return An instance of {@link ObjectGenerator} which reads CSV file.
     */
    public static ObjectGenerator<Map<String, String>> csv(String path, char delimiter, String recordSeparator,
            boolean trim, Character quote, char commentMarker, boolean ignoreEmptyLines, String nullString) {
        return wrap(new CsvReaderValue(new CSVParserSettings(path, delimiter, recordSeparator, trim, quote,
                commentMarker, ignoreEmptyLines, nullString)));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     * JSON.
     *
     * @param generator Instance of {@link ObjectGenerator} which value will be converted to JSON.
     * @return An instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     *         JSON.
     */
    public static ObjectGenerator<String> json(ObjectGenerator<?> generator) {
        return wrap(new JsonTransformer(generator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     * time format. For format options, see {@link java.time.format.DateTimeFormatter DateTimeFormatter}.
     *
     * @param format Format string.
     * @param generator Instance of {@link ObjectGenerator} which value will be formated to string. It must return
     *            {@link Long}, {@link Date}, {@link LocalDate} or {@link LocalDateTime}.
     * @param <T> Type of value count pair contains.
     * @return An instance of {@link ObjectGenerator} which converts specified instance of {@link ObjectGenerator} to
     *         time format.
     */
    public static <T> ObjectGenerator<String> time(String format, ObjectGenerator<T> generator) {
        return wrap(new TimeFormatTransformer(format, generator.value));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which extracts property with given name and type from specified
     * instance of {@link ObjectGenerator}.
     *
     * @param keyName Name of the key.
     * @param keyType Type of the key.
     * @param generator Instance of {@link ObjectGenerator} from which value will be extracted.
     * @param <T> Type object generator will return.
     * @return An instance of {@link ObjectGenerator} which extracts property.
     */
    public static <T> ObjectGenerator<T> get(String keyName, Class<T> keyType, ObjectGenerator<?> generator) {
        return wrap(new GetterTransformer<>(keyName, keyType, generator.value));
    }

    /**
     * Creates an instance of {@link CountPair}. This is a helper method that is useful for following methods:<br>
     * {@link #exactly(CountPair...)}<br>
     * {@link #exactly(List)}<br>
     *
     * @param value The value.
     * @param count Represents how many times value can be used.
     * @param <T> Type of value count pair contains.
     * @return An instance of {@link CountPair}.
     */
    public static <T> CountPair<T> countPair(T value, int count) {
        return new CountPair<>(value(value), count);
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     * Values are generated using weighted distribution until depleted. When all values are generated specified number
     * of times and {@link ObjectGenerator#next()} is invoked,
     * {@link io.smartcat.ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException
     * ExactWeightedValueDepletedException} is thrown.
     *
     * @param pairs List of values with corresponding count.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> exactly(CountPair<T>... pairs) {
        return exactly(Arrays.asList(pairs));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     * Values are generated using weighted distribution until depleted. When all values are generated specified number
     * of times and {@link ObjectGenerator#next()} is invoked,
     * {@link io.smartcat.ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException
     * ExactWeightedValueDepletedException} is thrown.
     *
     * @param pairs List of values with corresponding counts.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates each value exactly specified number of times.
     */
    public static <T> ObjectGenerator<T> exactly(List<CountPair<T>> pairs) {
        return wrap(new ExactWeightedValue<>(unwrapCountPairs(pairs)));
    }

    /**
     * Creates an instance of {@link WeightPair}. This is a helper method that is useful for following methods:<br>
     * {@link #weighted(WeightPair...)}<br>
     * {@link #weighted(List)}<br>
     *
     * @param value The value.
     * @param weight Represents distribution weight of this value.
     * @param <T> Type of value weight pair contains.
     * @return An instance of {@link CountPair}.
     */
    public static <T> WeightPair<T> weightPair(T value, double weight) {
        return new WeightPair<>(value(value), weight);
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     *
     * @param pairs List of values with corresponding weights.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     */
    @SafeVarargs
    public static <T> ObjectGenerator<T> weighted(WeightPair<T>... pairs) {
        return weighted(Arrays.asList(pairs));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     *
     * @param pairs List of values with corresponding weights.
     * @param <T> Type instance of {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator} which generates specified values distributed by their weights.
     */
    public static <T> ObjectGenerator<T> weighted(List<WeightPair<T>> pairs) {
        return wrap(new WeightedValue<>(unwrapWeightPairs(pairs)));
    }

    private static <T> ObjectGenerator<T> wrap(Value<T> value) {
        return new ObjectGenerator<>(value);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static List<Value<?>> unwrapRaw(List<Object> objects) {
        List result = unwrap(objects);
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<Value<T>> unwrap(List<T> objects) {
        List<Value<T>> result = new ArrayList<>();
        for (T object : objects) {
            if (object instanceof ObjectGenerator) {
                result.add(((ObjectGenerator) object).value);
            } else {
                result.add(ConstantValue.of(object));
            }
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<CountValuePair<T>> unwrapCountPairs(List<CountPair<T>> pairs) {
        List<CountValuePair<T>> result = new ArrayList<>();
        for (CountPair pair : pairs) {
            result.add(new CountValuePair<>(pair.value, pair.count));
        }
        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T> List<WeightedValuePair<T>> unwrapWeightPairs(List<WeightPair<T>> pairs) {
        List<WeightedValuePair<T>> result = new ArrayList<>();
        for (WeightPair pair : pairs) {
            result.add(new WeightedValuePair<>(pair.value, pair.weight));
        }
        return result;
    }

    @SuppressWarnings({ "unchecked" })
    private static <T> Value<T> value(T object) {
        return object instanceof ObjectGenerator ? ((ObjectGenerator<T>) object).value : ConstantValue.of(object);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static <T extends Comparable<T>> io.smartcat.ranger.core.Range<T> toRange(Range range) {
        return new io.smartcat.ranger.core.Range<>((T) range.beginning, (T) range.end);
    }

    /**
     * Range.
     *
     * @param <T> Type of the range.
     */
    private static class Range<T> {

        private final T beginning;
        private final T end;

        private Range(T beginning, T end) {
            if (beginning == null) {
                throw new IllegalArgumentException("Beginning cannot be null.");
            }
            if (end == null) {
                throw new IllegalArgumentException("End cannot be null.");
            }
            this.beginning = beginning;
            this.end = end;
        }
    }

    /**
     * Holds information of number of times a value can be generated.
     *
     * @param <T> Type of value count pair contains.
     */
    private static class CountPair<T> {

        private final Value<T> value;
        private final int count;

        private CountPair(Value<T> value, int count) {
            this.value = value;
            this.count = count;
        }
    }

    /**
     * Holds information value's distribution weight.
     *
     * @param <T> Type of value weight pair contains.
     */
    private static class WeightPair<T> {

        private final Value<T> value;
        private final double weight;

        private WeightPair(Value<T> value, double weight) {
            this.value = value;
            this.weight = weight;
        }
    }
}
