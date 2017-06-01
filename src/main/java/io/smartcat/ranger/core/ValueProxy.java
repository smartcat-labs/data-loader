package io.smartcat.ranger.core;

/**
 * Proxy around value that can cache value and can reset cache.
 *
 * @param <T> Type this value would evaluate to.
 */
public class ValueProxy<T> implements Value<T> {

    private Value<T> delegate;
    private T evaluatedValue;
    private boolean isEvaluated;

    /**
     * Constructs proxy without delegate.
     */
    public ValueProxy() {
        this.delegate = null;
        this.isEvaluated = false;
    }

    /**
     * Constructs proxy with specified <code>delegate</code>.
     *
     * @param delegate Value which will be evaluated and cached.
     */
    public ValueProxy(Value<T> delegate) {
        this.delegate = delegate;
        this.isEvaluated = false;
    }

    /**
     * Sets value to this proxy.
     *
     * @param delegate Value which will be evaluated and cached.
     */
    public void setDelegate(Value<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T eval() {
        if (delegate == null) {
            throw new IllegalStateException("Delegate value has not been set.");
        }
        if (!isEvaluated) {
            evaluatedValue = delegate.eval();
            isEvaluated = true;
        }
        return evaluatedValue;
    }

    /**
     * Resets cache enforcing reevaluation of delegate value when next {@link #eval()} is invoked.
     */
    public void reset() {
        isEvaluated = false;
    }
}
