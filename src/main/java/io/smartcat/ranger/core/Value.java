package io.smartcat.ranger.core;

/**
 * Root of type hierarchy. It can evaluate to a value.
 *
 * @param <T> Type value would evaluate to.
 */
public abstract class Value<T> {

    /**
     * Indicates whether value is evaluated or not.
     */
    protected boolean evaluated = false;

    /**
     * The value.
     */
    protected T val;

    /**
     * Returns a value depending on concrete implementation.
     *
     * @return A value depending on concrete implementation.
     */
    public T get() {
        if (!evaluated) {
            eval();
            evaluated = true;
        }
        return val;
    }

    /**
     * Enforces reevaluation of value for next {@link #get()} invocation.
     */
    public void reset() {
        evaluated = false;
    }

    /**
     * Evaluates {@link #val} variable.
     */
    protected void eval() {
    }
}
