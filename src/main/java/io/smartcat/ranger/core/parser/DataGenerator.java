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

    private Value<?> value;

    private DataGenerator(Value<?> value) {
        this.value = value;
    }

    /**
     * Returns next value.
     *
     * @return Next value.
     */
    public Object next() {
        value.reset();
        return value.get();
    }

    /**
     * Builder for {@link DataGenerator}.
     */
    public static class Builder {

        private final Map<String, Object> config;
        private final String outputValueName;
        private final Map<String, ValueProxy<?>> proxyValues;
        private final ValueExpressionParser parser;
        private final ReportingParseRunner<Value<?>> parseRunner;

        /**
         * Constructs Builder that will build {@link DataGenerator}.
         *
         * @param config Data generator configuration.
         * @param outputValueName Name of output value.
         */
        public Builder(Map<String, Object> config, String outputValueName) {
            this.config = config;
            this.outputValueName = outputValueName;
            this.proxyValues = new HashMap<>();
            this.parser = Parboiled.createParser(ValueExpressionParser.class, proxyValues);
            this.parseRunner = new ReportingParseRunner<>(parser.value());
        }

        /**
         * Builds {@link DataGenerator} based on provided configuration.
         *
         * @return Instance of {@link DataGenerator}.
         */
        public DataGenerator build() {
            createProxies();
            parseCompositeValue("", config);
            return new DataGenerator(proxyValues.get(outputValueName));
        }

        private void createProxies() {
            for (Map.Entry<String, Object> entry : config.entrySet()) {
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
                String fullName = parentName.isEmpty() ? property : parentName + "." + property;
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
                return PrimitiveValue.of(def);
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
}
