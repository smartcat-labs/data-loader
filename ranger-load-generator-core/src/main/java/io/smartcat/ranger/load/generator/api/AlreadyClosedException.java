package io.smartcat.ranger.load.generator.api;

/**
 * Signals that method has been called on already closed object.
 */
public class AlreadyClosedException extends IllegalStateException {

    private static final long serialVersionUID = -2862469865231955799L;

    /**
     * Constructs an AlreadyClosedException with no detail message. A detail message is a String that describes this
     * particular exception.
     */
    public AlreadyClosedException() {
    }

    /**
     * Constructs an AlreadyClosedException with the specified detail message. A detail message is a String that
     * describes this particular exception.
     *
     * @param message the String that contains a detailed message.
     */
    public AlreadyClosedException(String message) {
        super(message);
    }
}
