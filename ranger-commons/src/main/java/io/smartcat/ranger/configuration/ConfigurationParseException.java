package io.smartcat.ranger.configuration;

/**
 * Indicates error while parsing configuration.
 */
public class ConfigurationParseException extends ConfigurationException {

    private static final long serialVersionUID = -7101805420460054579L;

    /**
     * Default constructor.
     */
    public ConfigurationParseException() {
    }

    /**
     * Constructor.
     *
     * @param message Error message.
     * @param cause Exception cause.
     * @param enableSuppression controls exception suppression.
     * @param writableStackTrace stack trace.
     */
    public ConfigurationParseException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * Constructor.
     *
     * @param message Error message.
     * @param cause Exception cause.
     */
    public ConfigurationParseException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor.
     *
     * @param message Error message.
     */
    public ConfigurationParseException(String message) {
        super(message);
    }

    /**
     * Constructor.
     *
     * @param cause Exception cause.
     */
    public ConfigurationParseException(Throwable cause) {
        super(cause);
    }
}
