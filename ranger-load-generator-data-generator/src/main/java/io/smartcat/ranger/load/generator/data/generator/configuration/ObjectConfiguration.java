package io.smartcat.ranger.load.generator.data.generator.configuration;

import java.util.List;

/**
 * Configuration of an object consisting of configuration for each field.
 */
public class ObjectConfiguration {

    private int numberOfObjects;
    private List<Field> fields;

    /**
     * Returns number of objects that will be generated.
     *
     * @return Number of objects that will be generated.
     */
    public int getNumberOfObjects() {
        return numberOfObjects;
    }

    /**
     * Sets the number of objects that will be generated.
     *
     * @param numberOfObjects Number of objects that will be generated.
     */
    public void setNumberOfObjects(int numberOfObjects) {
        this.numberOfObjects = numberOfObjects;
    }

    /**
     * Returns list of fields for this object configuration.
     *
     * @return List of fields for this object configuration.
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Sets list of fields for this object configuration.
     *
     * @param fields List of fields for this object configuration.
     */
    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
