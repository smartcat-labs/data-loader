package io.smartcat.ranger.core;

/**
 * Circular range value for byte type.
 */
public class CircularRangeValueByte extends CircularRangeValue<Byte> {

    /**
     * Constructs byte circular range value with specified <code>range</code> and <code>increment</code>.
     *
     * @param range Range.
     * @param increment Increment.
     */
    public CircularRangeValueByte(Range<Byte> range, Byte increment) {
        super(range, increment);
    }

    @Override
    protected Byte zero() {
        return 0;
    }

    @Override
    protected boolean isIncrementGreaterThanRangeSize() {
        return Math.abs(range.getBeginning() - range.getEnd()) <= Math.abs(increment);
    }

    @Override
    protected Byte peekNextValue() {
        return (byte) (val + increment);
    }
}
