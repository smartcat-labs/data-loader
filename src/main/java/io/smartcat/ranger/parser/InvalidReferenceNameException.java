package io.smartcat.ranger.parser;

/**
 * Signals that invalid reference name is used within configuration.
 */
public class InvalidReferenceNameException extends RuntimeException {

    private static final long serialVersionUID = -5468713809934047143L;

    /**
     * Constructs an {@link InvalidReferenceNameException} with {@code null} as its detail message.
     */
    public InvalidReferenceNameException() {
    }

    /**
     * Constructs an {@link InvalidReferenceNameException} with message containing the invalid reference name.
     *
     * @param referenceName Name of the invalid reference name.
     */
    public InvalidReferenceNameException(String referenceName) {
        super("Reference with name '" + referenceName + "' not defined within configuration.");
    }
}
