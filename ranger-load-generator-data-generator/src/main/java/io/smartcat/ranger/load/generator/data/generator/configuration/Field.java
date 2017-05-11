package io.smartcat.ranger.load.generator.data.generator.configuration;

/**
 * Configuration for a field for an {@link io.smartcat.ranger.data.generator.ObjectGenerator}.
 */
public class Field {

    private String name;
    private String values;

    /**
     * Returns name of the field.
     *
     * @return Name of the field.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of the field.
     *
     * @param name Name of the field.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns values the field can have.
     *
     * @return Values the field can have.
     */
    public String getValues() {
        return values;
    }

    /**
     * Sets values the field can have.
     *
     * @param values Values the field can have.
     */
    public void setValues(String values) {
        this.values = values;
    }
}
