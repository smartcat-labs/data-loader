package io.smartcat.ranger.core;

/**
 * Signals that range bounds are not valid.
 */
public class InvalidRangeBoundsException extends RuntimeException {

    private static final long serialVersionUID = 5113906580849213896L;

    /**
     * Constructs {@link InvalidRangeBoundsException} with specified detail message.
     *
     * @param message The detail message.
     */
    public InvalidRangeBoundsException(String message) {
        super(message);
    }
}
