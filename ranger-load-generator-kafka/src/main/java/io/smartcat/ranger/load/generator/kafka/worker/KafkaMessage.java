package io.smartcat.ranger.load.generator.kafka.worker;

/**
 * Key/Value pair to be sent to Kafka.
 */
public class KafkaMessage {

    private final String key;
    private final String value;

    /**
     * Constructs message with specified <code>key</code> and <code>value</code>.
     * @param key Key of the message.
     * @param value Value of the message.
     */
    public KafkaMessage(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Returns key of the message.
     * @return key of the message.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns value of the message.
     * @return value of the message.
     */
    public String getValue() {
        return value;
    }
}
