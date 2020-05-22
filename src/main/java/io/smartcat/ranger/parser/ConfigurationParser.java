package io.smartcat.ranger.parser;

import io.smartcat.ranger.ObjectGenerator;
import io.smartcat.ranger.core.CompositeValue;
import io.smartcat.ranger.core.ConstantValue;
import io.smartcat.ranger.core.TypeConverterValue;
import io.smartcat.ranger.core.Value;
import io.smartcat.ranger.core.ValueProxy;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Constructs {@link ObjectGenerator} out of parsed configuration.
 */
public class ConfigurationParser {

    private static final String VALUES = "values";
    private static final String OUTPUT = "output";

    private final Map<String, Object> values;
    private final Object outputExpression;
    private Map<String, ValueProxy<?>> proxyValues;
    private ValueExpressionParser parser;
    private ReportingParseRunner<Value<?>> parseRunner;

    /**
     * Constructs Builder that will build {@link ConfigurationParser}.
     *
     * @param config Data generator configuration.
     */
    @SuppressWarnings("unchecked")
    public ConfigurationParser(Map<String, Object> config) {
        checkSectionExistence(config, VALUES);
        checkSectionExistence(config, OUTPUT);
        this.values = (Map<String, Object>) config.get(VALUES);
        this.outputExpression = config.get(OUTPUT);
    }

    /**
     * Creates an instance of {@link ObjectGenerator} based on provided configuration.
     *
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return An instance of {@link ObjectGenerator}.
     */
    @SuppressWarnings({ "unchecked" })
    public <T> ObjectGenerator<T> build() {
        buildModel();
        return new ObjectGenerator<>((Value<T>) parseSimpleValue("", outputExpression));
    }

    /**
     * Creates an instance of {@link ObjectGenerator} based on provided configuration.. Resulting
     * {@link ObjectGenerator} will try to convert configured output to specified <code>objectType</code>.
     *
     * @param objectType Type of object to which conversion will be attempted.
     * @param <T> Type of object {@link ObjectGenerator} will generate.
     * @return Instance of {@link ObjectGenerator}.
     */
    public <T> ObjectGenerator<T> build(Class<T> objectType) {
        buildModel();
        return new ObjectGenerator<>(new TypeConverterValue<>(objectType, parseSimpleValue("", outputExpression)));
    }

    private void buildModel() {
        this.proxyValues = new HashMap<>();
        this.parser = Parboiled.createParser(ValueExpressionParser.class, proxyValues);
        this.parseRunner = new ReportingParseRunner<>(parser.value());
        if (values != null) {
            createProxies();
            parseValues();
        }
    }

    private void checkSectionExistence(Map<String, Object> config, String name) {
        if (!config.containsKey(name)) {
            throw new RuntimeException("Configuration must contain '" + name + "' section.");
        }
    }

    private void createProxies() {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            createProxy(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    private void createProxy(String key, Object value) {
        proxyValues.put(key, new ValueProxy<>());
        if (value instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                createProxy(key + "." + entry.getKey(), entry.getValue());
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void parseValues() {
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            Value<?> val = parse(entry.getKey(), entry.getValue());
            ValueProxy proxy = proxyValues.get(entry.getKey());
            proxy.setDelegate(val);
            entry.setValue(proxy);
        }
    }

    @SuppressWarnings("unchecked")
    private Value<?> parse(String parentName, Object def) {
        if (def instanceof Map) {
            return parseCompositeValue(parentName, (Map<String, Object>) def);
        } else {
            return parseSimpleValue(parentName, def);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private Value<?> parseCompositeValue(String parentName, Map<String, Object> def) {
        Map<String, Value<?>> values = new HashMap<>();
        for (String property : def.keySet()) {
            String fullName = parentName + "." + property;
            Value<?> val = parse(fullName, def.get(property));
            ValueProxy proxy = proxyValues.get(fullName);
            proxy.setDelegate(val);
            values.put(property, proxy);
        }
        return new CompositeValue(values);
    }

    private Value<?> parseSimpleValue(String parentName, Object def) {
        // handle String as expression and all other types as primitives
        if (def instanceof String) {
            parser.setParentName(stripOffLastReference(parentName));
            ParsingResult<Value<?>> result = parseRunner.run((String) def);
            return result.valueStack.pop();
        } else {
            return ConstantValue.of(def);
        }
    }

    private String stripOffLastReference(String name) {
        if (!name.contains(".")) {
            return "";
        } else {
            return name.substring(0, name.lastIndexOf('.'));
        }
    }
}
