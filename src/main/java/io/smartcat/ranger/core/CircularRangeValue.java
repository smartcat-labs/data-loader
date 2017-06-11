package io.smartcat.ranger.core;

/**
 * Base circular range value class.Generates values within specified <code>range</code> from the beginning to the end
 * with specified <code>increment</code>.
 *
 * @param <T> Type this value would evaluate to.
 */
public abstract class CircularRangeValue<T extends Number & Comparable<T>> extends Value<T> {

    /**
     * Range.
     */
    protected final Range<T> range;

    /**
     * Increment.
     */
    protected final T increment;

    /**
     * Constructs circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValue(Range<T> range, T increment) {
        if (range == null) {
            throw new IllegalArgumentException("Range cannot be null.");
        }
        if (increment == null || increment.compareTo(zero()) == 0) {
            throw new IllegalArgumentException("Increment cannot be null nor zero.");
        }
        if (range.isEmpty()) {
            throw new IllegalArgumentException("Range cannot be empty.");
        }
        if (range.isIncreasing() && increment.compareTo(zero()) < 0) {
            throw new IllegalArgumentException("If range is increasing, increment must be positive.");
        }
        if (range.isDecreasing() && increment.compareTo(zero()) > 0) {
            throw new IllegalArgumentException("If range is decreasing, increment must be negative.");
        }
        this.range = range;
        this.increment = increment;
        this.val = range.getBeginning();
        this.evaluated = true;
        if (isIncrementGreaterThanRangeSize()) {
            throw new IllegalArgumentException("Range size must be greater than increment.");
        }
    }

    @Override
    protected void eval() {
        T nextValue = peekNextValue();
        if (isValueInBounds(nextValue)) {
            val = nextValue;
        } else {
            val = range.getBeginning();
        }
    }

    private boolean isValueInBounds(T value) {
        return (range.isIncreasing() && value.compareTo(range.getEnd()) <= 0)
            || (range.isDecreasing() && value.compareTo(range.getEnd()) >= 0);
    }

    /**
     * Returns {@code 0} value represented within {@code <T>} type.
     *
     * @return {@code 0} value represented within {@code <T>} type.
     */
    protected abstract T zero();

    /**
     * Indicates whether increment value is greater than range size or not.
     *
     * @return True if increment value is greater than range size, otherwise false.
     */
    protected abstract boolean isIncrementGreaterThanRangeSize();

    /**
     * Returns next value without changing state of <code>currentValue</code>.
     *
     * @return Next value without changing state of <code>currentValue</code>.
     */
    protected abstract T peekNextValue();
}
