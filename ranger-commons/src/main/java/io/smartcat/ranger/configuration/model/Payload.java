package io.smartcat.ranger.configuration.model;

/**
 * Payload object.
 */
public abstract class Payload {

    /**
     * Value of the payload.
     */
    protected String value;

    /**
     * Constructor.
     */
    public Payload() {
    }

    /**
     * Constructor.
     *
     * @param value Payload value.
     */
    public Payload(String value) {
        this.value = value;
    }

    /**
     * Returns payload value.
     *
     * @return Payload value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets payload value.
     *
     * @param value Payload value to set.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
