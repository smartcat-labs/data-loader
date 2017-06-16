package io.smartcat.ranger.core;

/**
 * Represents range of values.
 *
 * @param <T> Type of values.
 */
public class Range<T extends Comparable<T>> {

    private final T beginning;
    private final T end;

    /**
     * Constructs range with specified <code>beginning</code> and <code>end</code>.
     *
     * @param beginning The beginning of the range.
     * @param end The end of the range.
     */
    public Range(T beginning, T end) {
        if (beginning == null) {
            throw new InvalidRangeBoundsException("Beginning of the range cannot be null.");
        }
        if (end == null) {
            throw new InvalidRangeBoundsException("End of the range cannot be null.");
        }
        if (!beginning.getClass().equals(end.getClass())) {
            throw new InvalidRangeBoundsException("Beginning and end must be of the same type.");
        }
        this.beginning = beginning;
        this.end = end;
    }

    /**
     * Returns the beginning of the range.
     *
     * @return The beginning of the range.
     */
    public T getBeginning() {
        return beginning;
    }

    /**
     * Returns the end of the range.
     *
     * @return The end of the range.
     */
    public T getEnd() {
        return end;
    }

    /**
     * Indicates whether range is empty (the beginning and the end are equal) or not.
     *
     * @return True if range is empty (the beginning and the end are equal), otherwise false.
     */
    public boolean isEmpty() {
        return beginning.compareTo(end) == 0;
    }

    /**
     * Indicates whether range is increasing (the beginning is less than the end) or not.
     *
     * @return True if range is increasing (the beginning is less than the end), otherwise false.
     */
    public boolean isIncreasing() {
        return beginning.compareTo(end) < 0;
    }

    /**
     * Indicates whether range is decreasing (the beginning is greater than the end) or not.
     *
     * @return True if range is decreasing (the beginning is greater than the end), otherwise false.
     */
    public boolean isDecreasing() {
        return beginning.compareTo(end) > 0;
    }
}
