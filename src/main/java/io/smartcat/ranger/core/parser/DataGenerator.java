package io.smartcat.ranger.core.parser;

import io.smartcat.ranger.core.CompositeValue;
import io.smartcat.ranger.core.PrimitiveValue;
import io.smartcat.ranger.core.Value;
import io.smartcat.ranger.core.ValueProxy;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates data specified by configuration.
 */
public class DataGenerator {

    private CompositeValue outputValue;
    private Map<String, ValueProxy<?>> proxyValues;

    private DataGenerator(Map<String, ValueProxy<?>> proxyValues, String outputValueName) {
        Value<?> val = proxyValues.get(outputValueName);
        if (val instanceof CompositeValue) {
            this.outputValue = (CompositeValue) val;
        } else {
            this.outputValue = new CompositeValue();
            this.outputValue.setValue("output", val);
        }
        this.proxyValues = proxyValues;
    }

    /**
     * Returns next value.
     *
     * @return Next value.
     */
    public Map<String, Object> next() {
        Map<String, Object> output = outputValue.eval();
        reset();
        return output;
    }

    private void reset() {
        for (ValueProxy<?> proxy : proxyValues.values()) {
            proxy.reset();
        }
    }

    /**
     * Builds {@link DataGenerator} based on specified <code>config</code> and <code>outputValueName</code>.
     *
     * @param config Data generation configuration.
     * @param outputValueName Name of the output value.
     * @return An instance of {@link DataGenerator}.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static DataGenerator build(Map<String, Object> config, String outputValueName) {
        Map<String, ValueProxy<?>> proxyValues = new HashMap<>();
        ValueExpressionParser parser = Parboiled.createParser(ValueExpressionParser.class);
        parser.setProxyValues(proxyValues);
        ReportingParseRunner<Value<?>> parseRunner = new ReportingParseRunner<>(parser.value());

        for (Map.Entry<String, Object> entry : config.entrySet()) {
            String valueName = entry.getKey();
            Object valueDef = entry.getValue();
            Value<?> value = parse(valueDef, parseRunner, proxyValues);
            ValueProxy proxy = obtainProxy(valueName, proxyValues);
            proxy.setDelegate(value);
        }

        return new DataGenerator(proxyValues, outputValueName);
    }

    private static ValueProxy<?> obtainProxy(String name, Map<String, ValueProxy<?>> proxyValues) {
        ValueProxy<?> proxy = proxyValues.get(name);
        if (proxy == null) {
            proxy = new ValueProxy<>();
            proxyValues.put(name, proxy);
        }
        return proxy;
    }

    @SuppressWarnings("unchecked")
    private static Value<?> parse(Object def, ReportingParseRunner<Value<?>> parseRunner,
            Map<String, ValueProxy<?>> proxyValues) {
        if (def instanceof Map) {
            return parseCompositeValue((Map<String, Object>) def, parseRunner, proxyValues);
        } else {
            return parseSimpleValue(def, parseRunner, proxyValues);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Value<?> parseCompositeValue(Map<String, Object> def, ReportingParseRunner<Value<?>> parseRunner,
            Map<String, ValueProxy<?>> proxyValues) {
        Map<String, Value<?>> values = new HashMap<>();
        for (String property : def.keySet()) {
            Value<?> val = parse(def.get(property), parseRunner, proxyValues);
            ValueProxy proxy = obtainProxy(property, proxyValues);
            proxy.setDelegate(val);
            values.put(property, proxy);
        }
        return new CompositeValue(values);
    }

    private static Value<?> parseSimpleValue(Object def, ReportingParseRunner<Value<?>> parseRunner,
            Map<String, ValueProxy<?>> proxyValues) {
        // handle String as expression and all other types as primitives
        if (def instanceof String) {
            ParsingResult<Value<?>> result = parseRunner.run((String) def);
            return result.valueStack.pop();
        } else {
            return PrimitiveValue.of(def);
        }
    }
}
