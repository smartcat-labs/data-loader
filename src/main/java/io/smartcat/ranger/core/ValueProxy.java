package io.smartcat.ranger.core;

/**
 * Proxy around value that can cache value and can reset cache.
 *
 * @param <T> Type this value would evaluate to.
 */
public class ValueProxy<T> extends Value<T> {

    private Value<T> delegate;

    /**
     * Constructs proxy without delegate.
     */
    public ValueProxy() {
    }

    /**
     * Constructs proxy with specified <code>delegate</code>.
     *
     * @param delegate Value which will be evaluated and cached.
     */
    public ValueProxy(Value<T> delegate) {
        setDelegate(delegate);
    }

    /**
     * Sets value to this proxy.
     *
     * @param delegate Value which will be evaluated and cached.
     */
    public void setDelegate(Value<T> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Delegate cannot be null.");
        }
        this.delegate = delegate;
    }

    @Override
    public void reset() {
        super.reset();
        checkDelegate();
        delegate.reset();
    }

    @Override
    protected void eval() {
        checkDelegate();
        val = delegate.get();
    }

    private void checkDelegate() {
        if (delegate == null) {
            throw new DelegateNotSetException();
        }
    }

    /**
     * Signals that delegate is not set.
     */
    public static class DelegateNotSetException extends RuntimeException {

        private static final long serialVersionUID = 6257779717961934851L;

        /**
         * Constructs {@link DelegateNotSetException} with default message.
         */
        public DelegateNotSetException() {
            super("Delegate not set for ValueProxy.");
        }
    }
}
