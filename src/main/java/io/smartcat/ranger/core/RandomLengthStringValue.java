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
public class RandomLengthStringValue extends Value<String> {

    private static final List<Range<Character>> DEFAULT_RANGES = Arrays.asList(new Range<Character>('a', 'z'),
            new Range<Character>('A', 'Z'), new Range<Character>('0', '9'));

    private final int length;
    private final List<Character> possibleCharacters;
    private final Distribution distribution;

    /**
     * Constructs random length string value with specified <code>length</code> and default character range.
     *
     * @param length Length of generated string.
     */
    public RandomLengthStringValue(int length) {
        this(length, DEFAULT_RANGES);
    }

    /**
     * Constructs random length string value with specified <code>length</code> and specified <code>charRanges</code>.
     *
     * @param length Length of generated string.
     * @param charRanges Ranges of characters from which string will be constructed.
     */
    public RandomLengthStringValue(int length, List<Range<Character>> charRanges) {
        if (length < 1) {
            throw new IllegalArgumentException("Length must be positive number.");
        }
        this.length = length;
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
    protected void eval() {
        char[] chars = new char[length];
        for (int i = 0; i < length; i++) {
            chars[i] = possibleCharacters.get(distribution.nextInt(possibleCharacters.size()));
        }
        val = new String(chars);
    }
}
