package io.smartcat.ranger.core;

/**
 * Circular range value for short type.
 */
public class CircularRangeValueShort extends CircularRangeValue<Short> {

    /**
     * Constructs short circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueShort(Range<Short> range, Short increment) {
        super(range, increment);
    }

    @Override
    protected Short zero() {
        return 0;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) <= Math.abs(increment);
    }

    @Override
    protected Short peekNextValue() {
        return (short) (val + increment);
    }
}
