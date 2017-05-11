package io.smartcat.ranger.configuration.model;

/**
 * Payload with key.
 */
public class KafkaPayload extends Payload {

    /**
     * Key of the payload.
     */
    protected String key;

    /**
     * Constructor.
     */
    public KafkaPayload() {
    }

    /**
     * Constructor.
     *
     * @param key Key of the payload.
     * @param value Value of the payload.
     */
    public KafkaPayload(String key, String value) {
        super(value);
        this.key = key;
    }

    /**
     * Returns payload key.
     *
     * @return Payload key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets paylaod key.
     *
     * @param key Payload key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }
}
