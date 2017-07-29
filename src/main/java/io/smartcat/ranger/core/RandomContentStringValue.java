package io.smartcat.ranger.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.smartcat.ranger.distribution.Distribution;
import io.smartcat.ranger.distribution.UniformDistribution;

/**
 * Generates random strings of specified <code>length</code> and from specified character ranges.
 */
public class RandomContentStringValue extends Value<String> {

    private static final List<Range<Character>> DEFAULT_RANGES = Arrays.asList(new Range<Character>('a', 'z'),
            new Range<Character>('A', 'Z'), new Range<Character>('0', '9'));

    private final Value<Integer> lengthValue;
    private final List<Character> possibleCharacters;
    private final Distribution distribution;

    /**
     * Constructs random content string value with specified <code>lengthValue</code> and default character range.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     */
    public RandomContentStringValue(Value<Integer> lengthValue) {
        this(lengthValue, DEFAULT_RANGES);
    }

    /**
     * Constructs random content string value with specified <code>lengthValue</code> and specified
     * <code>charRanges</code>.
     *
     * @param lengthValue Value that returns integer which represents length of generated string. It should never
     *            generate length that is less than 1.
     * @param charRanges Ranges of characters from which string will be constructed.
     */
    public RandomContentStringValue(Value<Integer> lengthValue, List<Range<Character>> charRanges) {
        if (lengthValue == null) {
            throw new IllegalArgumentException("lengthValue cannot be null.");
        }
        this.lengthValue = lengthValue;
        Set<Character> chars = new HashSet<>();
        for (Range<Character> range : charRanges) {
            if (!range.isIncreasing()) {
                throw new IllegalArgumentException("All ranges must be increasing.");
            }
            for (Character c = range.getBeginning(); c <= range.getEnd(); c++) {
                chars.add(c);
            }
        }
        possibleCharacters = new ArrayList<>(chars);
        distribution = new UniformDistribution();
    }

    @Override
    public void reset() {
        super.reset();
        lengthValue.reset();
    }

    @Override
    protected void eval() {
        int length = lengthValue.get();
        if (length < 1) {
            throw new RuntimeException("Generated length cannot be less than 1, but was: " + length);
        }
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = possibleCharacters.get(distribution.nextInt(possibleCharacters.size()));
        }
        val = new String(chars);
    }
}
