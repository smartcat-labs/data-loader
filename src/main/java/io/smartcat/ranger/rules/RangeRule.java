package io.smartcat.ranger.rules;

import java.util.ArrayList;
import java.util.List;

import io.smartcat.ranger.distribution.Distribution;

/**
 * Abstract Range rule class.
 *
 * @param <T> Type of value which will be generated.
 */
public abstract class RangeRule<T extends Comparable<T>> implements Rule<T> {

    /**
     *  Definition of the range: e.g [a,b,c,d] : a &lt; b &lt;= c &lt; d is a set of ranges: {[a,b),[c,d)}.
     */
    protected final List<T> rangeMarkers;

    /**
     * Range edges.
     */
    protected final List<T> rangeEdges;

    /**
     * Distribution.
     */
    protected final Distribution distribution;

    /**
     * Sets the allowed ranges of {@code <T>} for the field with {@code fieldName}. The ranges are defined by a list
     * of {@code <T>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
     * <code>(n % 2) == 0</code>;
     *
     * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
     * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
     * denotes exclusive end of the range.
     *
     * @param rangeMarkers List of {@code <T>} that denotes the ranges.
     *
     * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
     */
    public RangeRule(List<T> rangeMarkers) {
        this(rangeMarkers, DEFAULT_DISTRIBUTION);
    }

    /**
     * Sets the allowed ranges of {@code <T>} for the field with {@code fieldName}. The ranges are defined by a list
     * of {@code <T>} <code>A1, A2, ..., An</code> such that <code>A1 &lt; A2 &lt; ... &lt; An</code> and
     * <code>(n % 2) == 0</code>;
     *
     * The ranges defined by <code>A1, A2, ..., An</code> are: <code>[A1, A2), [A3, A4), ..., [A(n-1), An)</code>.
     * In each range <code>[Aj, Ak)</code> <code>Aj</code> denotes inclusive start of the range and <code>Ak</code>
     * denotes exclusive end of the range.
     *
     * @param rangeMarkers List of Doubles that denotes the ranges.
     * @param distribution Distribution to be used when generating values.
     *
     * @throws IllegalArgumentException if {@code rangeMarkers} is not strictly increasing array.
     */
    public RangeRule(List<T> rangeMarkers, Distribution distribution) {
        checkRangeInput(rangeMarkers);
        this.rangeMarkers = new ArrayList<>(rangeMarkers);
        this.rangeEdges = new ArrayList<>(rangeMarkers);
        this.distribution = distribution == null ? DEFAULT_DISTRIBUTION : distribution;
    }

    @Override
    public T next() {
        // ranges = [a,b,c,d]
        // =>
        // (a,b],(c,d]
        // 0 , 1

        if (!rangeEdges.isEmpty()) {
            return nextEdgeCase();
        }

        // generate random values
        int index = 0;
        if (rangeMarkers.size() > 2) {
            index = distribution.nextInt(rangeMarkers.size() / 2);
        }
        return nextValue(rangeMarkers.get(index * 2), rangeMarkers.get((index * 2) + 1));
    }

    /**
     * Returns next value of type {@code <T>}.
     *
     * @param lower Lower bound (inclusive).
     * @param upper Upper bound (exclusive).
     * @return Next value of type {@code <T>}.
     */
    protected abstract T nextValue(T lower, T upper);

    /**
     * Returns next edge case of type {@code <T>}.
     *
     * @return Next edge case of type {@code <T>}.
     */
    protected abstract T nextEdgeCase();

    private void checkRangeInput(List<T> rangeMarkers) {
        if (rangeMarkers == null) {
            throw new IllegalArgumentException("Rqanges cannot be null.");
        }
        if (rangeMarkers.isEmpty()) {
            throw new IllegalArgumentException("Ranges cannot be empty.");
        }
        if (rangeMarkers.size() % 2 != 0) {
            throw new IllegalArgumentException(
                    "Invalid ranges definition. Ranges must be defined with even number of elements.");
        }
        for (int i = 1; i < rangeMarkers.size(); i++) {
            T previousMarker = rangeMarkers.get(i - 1);
            T currentMarker = rangeMarkers.get(i);
            if (currentMarker.compareTo(previousMarker) <= 0) {
                throw new IllegalArgumentException(
                        "Invalid range bounds. Range definition must be stricly increasing.");
            }
        }
    }
}
