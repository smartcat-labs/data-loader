package io.smartcat.ranger.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.smartcat.ranger.core.*;
import org.parboiled.BaseParser;
import org.parboiled.Rule;

/**
 * Parser for configuration value expressions.
 */
public class ValueExpressionParser extends BaseParser<Object> {

    private static final String DISCRETE_VALUE_DELIMITER = "discreteValueDelimiter";
    private static final String CIRCULAR_VALUE_DELIMITER = "circularValueDelimiter";
    private static final String STRING_VALUE_DELIMITER = "stringValueDelimiter";

    private final Map<String, ValueProxy<?>> proxyValues;

    private String parentName;

    /**
     * Constructs parser with initial <code>proxyValues</code>.
     *
     * @param proxyValues Map containing proxy values by name.
     */
    public ValueExpressionParser(Map<String, ValueProxy<?>> proxyValues) {
        this.proxyValues = proxyValues;
    }

    /**
     * Sets parent name.
     *
     * @param parentName Parent name.
     */
    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    /**
     * Whitespace definition.
     *
     * @return Whitespace definition rule.
     */
    public Rule whitespace() {
        return AnyOf(" \t");
    }

    /**
     * Newline definition.
     *
     * @return Newline definition rule.
     */
    public Rule newline() {
        return AnyOf("\r\n");
    }

    /**
     * Comma definition.
     *
     * @return Comma definition rule.
     */
    public Rule comma() {
        return Sequence(ZeroOrMore(whitespace()), ",", ZeroOrMore(whitespace()));
    }

    /**
     * Sign definition.
     *
     * @return Sign definition rule.
     */
    public Rule sign() {
        return AnyOf("+-");
    }

    /**
     * Letter definition.
     *
     * @return Letter definition rule.
     */
    public Rule letter() {
        return FirstOf(CharRange('a', 'z'), CharRange('A', 'Z'));
    }

    /**
     * Digit definition.
     *
     * @return Digit definition rule.
     */
    public Rule digit() {
        return CharRange('0', '9');
    }

    /**
     * Letter or digit definition.
     *
     * @return Letter or digit definition rule.
     */
    public Rule letterOrDigit() {
        return FirstOf(letter(), digit());
    }

    /**
     * Unsigned integer definition.
     *
     * @return Unsigned integer definition rule.
     */
    public Rule unsignedIntegerLiteral() {
        return OneOrMore(digit());
    }

    /**
     * Exponent definition.
     *
     * @return Exponent definition rule.
     */
    public Rule exponent() {
        return Sequence(AnyOf("eE"), Optional(sign()), unsignedIntegerLiteral());
    }

    /**
     * Null value definition.
     *
     * @return Null value definition rule.
     */
    public Rule nullValue() {
        return Sequence("null()", push(new NullValue()));
    }

    /**
     * Long definition.
     *
     * @return Long definition rule.
     */
    public Rule longLiteral() {
        return Sequence(Sequence(Optional(sign()), unsignedIntegerLiteral()), push(Long.parseLong(match())));
    }

    /**
     * Long value definition.
     *
     * @return Long value definition rule.
     */
    public Rule longLiteralValue() {
        return Sequence(longLiteral(), push(PrimitiveValue.of((Long) pop())));
    }

    /**
     * Double definition.
     *
     * @return Double definition rule.
     */
    public Rule doubleLiteral() {
        return Sequence(
                Sequence(Optional(sign()),
                        FirstOf(Sequence(unsignedIntegerLiteral(), '.', unsignedIntegerLiteral(), Optional(exponent())),
                                Sequence('.', unsignedIntegerLiteral(), Optional(exponent())))),
                push(Double.parseDouble(match())));
    }

    /**
     * Double value definition.
     *
     * @return Double value definition rule.
     */
    public Rule doubleLiteralValue() {
        return Sequence(doubleLiteral(), push(PrimitiveValue.of((Double) pop())));
    }

    /**
     * Boolean value definition.
     *
     * @return Boolean value definition rule.
     */
    public Rule booleanLiteralValue() {
        return Sequence(FirstOf(FirstOf("True", "true"), FirstOf("False", "false")),
                push(PrimitiveValue.of(Boolean.parseBoolean(match()))));
    }

    /**
     * String definition.
     *
     * @return String definition rule.
     */
    public Rule stringLiteral() {
        return FirstOf(singleQuoteStringLiteral(), doubleQuoteStringLiteral());
    }

    /**
     * Naked string definition.
     *
     * @return Naked string definition rule.
     */
    public Rule nakedStringLiteral() {
        return Sequence(ZeroOrMore(TestNot(AnyOf("\r\n\"'\\")), ANY), push(match()));
    }

    /**
     * Single quote string definition.
     *
     * @return Single quote string definition rule.
     */
    public Rule singleQuoteStringLiteral() {
        return Sequence(Sequence("'", ZeroOrMore(TestNot(AnyOf("\r\n'\\")), ANY), "'"), push(trimOffEnds(match())));
    }

    /**
     * Double quote string definition.
     *
     * @return Double quote string definition rule.
     */
    public Rule doubleQuoteStringLiteral() {
        return Sequence(Sequence('"', ZeroOrMore(TestNot(AnyOf("\r\n\"\\")), ANY), '"'), push(trimOffEnds(match())));
    }

    /**
     * String value definition.
     *
     * @return String value definition rule.
     */
    public Rule stringLiteralValue() {
        return Sequence(FirstOf(stringLiteral(), nakedStringLiteral()), push(PrimitiveValue.of((String) pop())));
    }

    /**
     * Literal definition.
     *
     * @return Literal definition rule.
     */
    public Rule literalValue() {
        return FirstOf(nullValue(), doubleLiteralValue(), longLiteralValue(), booleanLiteralValue(),
                stringLiteralValue());
    }

    /**
     * Identifier definition.
     *
     * @return Identifier definition rule.
     */
    public Rule identifier() {
        return Sequence(Sequence(letter(), ZeroOrMore(letterOrDigit())), push(match()));
    }

    /**
     * Identifier definition which does not push match to value stack.
     *
     * @return Identifier definition rule.
     */
    public Rule identifierWithNoPush() {
        return Sequence(letter(), ZeroOrMore(letterOrDigit()));
    }

    /**
     * Value reference definition.
     *
     * @return Value reference definition rule.
     */
    public Rule valueReference() {
        return Sequence('$', Sequence(Sequence(identifierWithNoPush(), ZeroOrMore('.', identifierWithNoPush())),
                push(getValueProxy(match()))));
    }

    /**
     * Discrete value definition.
     *
     * @return Discrete value definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule discreteValue() {
        return Sequence(
                Sequence("random([", ZeroOrMore(whitespace()), push(DISCRETE_VALUE_DELIMITER), value(),
                        ZeroOrMore(comma(), value()), ZeroOrMore(whitespace()), "])"),
                push(new DiscreteValue(getValuesUpToDelimiter(DISCRETE_VALUE_DELIMITER))));
    }

    /**
     * Double range value definition.
     *
     * @return Double range value definition rule.
     */
    public Rule rangeValueDouble() {
        return Sequence(
                Sequence("random(", ZeroOrMore(whitespace()), FirstOf(doubleLiteral(), longLiteral()), "..",
                        FirstOf(doubleLiteral(), longLiteral()), ZeroOrMore(whitespace()), ")"),
                push(newDoubleRangeValue()));
    }

    /**
     * Long range value definition.
     *
     * @return Long range value definition rule.
     */
    public Rule rangeValueLong() {
        return Sequence(Sequence("random(", ZeroOrMore(whitespace()), longLiteral(), "..", longLiteral(),
                ZeroOrMore(whitespace()), ")"), push(new RangeValueLong((Long) pop(1), (Long) pop())));
    }

    /**
     * Range value definition.
     *
     * @return Range value definition rule.
     */
    public Rule rangeValue() {
        return FirstOf(rangeValueLong(), rangeValueDouble());
    }

    /**
     * UUID value definition.
     *
     * @return UUID value definition rule.
     */
    public Rule uuidValue() {
        return Sequence(fromStringLiteral("uuid()"), push(new UUIDValue()));
    }

    /**
     * Circular value definition.
     *
     * @return Circular value definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule circularValue() {
        return Sequence(
                Sequence("circular([", ZeroOrMore(whitespace()), push(CIRCULAR_VALUE_DELIMITER), value(),
                        ZeroOrMore(comma(), value()), ZeroOrMore(whitespace()), "])"),
                push(new CircularValue(getValuesUpToDelimiter(CIRCULAR_VALUE_DELIMITER))));
    }

    /**
     * Generator definition.
     *
     * @return Generator definition rule.
     */
    public Rule generator() {
        return FirstOf(discreteValue(), rangeValue(), uuidValue(), circularValue());
    }

    /**
     * String transformer definition.
     *
     * @return String transformer definition rule.
     */
    public Rule stringTransformer() {
        return Sequence(Sequence("string(", ZeroOrMore(whitespace()), stringLiteral(), push(STRING_VALUE_DELIMITER),
                ZeroOrMore(comma(), value()), ZeroOrMore(whitespace()), ")"), push(getToStringValue()));
    }

    /**
     * JSON transformer definition.
     *
     * @return JSON transformer definition rule.
     */
    public Rule jsonTransformer() {
        return Sequence("json(", ZeroOrMore(whitespace()), valueReference(), ZeroOrMore(whitespace()), ")",
                push(new JsonTransformer((Value<?>) pop())));
    }

    /**
     * Time format transformer definition.
     *
     * @return Time format transformer definition rule.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Rule timeFormatTransformer() {
        return Sequence(Sequence("time(", ZeroOrMore(whitespace()), stringLiteral(), comma(), value(),
                ZeroOrMore(whitespace()), ")"), push(new TimeFormatTransformer((String) pop(1), (Value) pop())));
    }

    /**
     * Transformer definition.
     *
     * @return Transformer definition rule.
     */
    public Rule transformer() {
        return FirstOf(stringTransformer(), jsonTransformer(), timeFormatTransformer());
    }

    /**
     * Value definition.
     *
     * @return Value definition rule.
     */
    public Rule value() {
        return FirstOf(valueReference(), generator(), transformer(), literalValue());
    }

    /**
     * Creates {@link RangeValueDouble} out of values from value stack.
     *
     * @return Instance of {@link RangeValueDouble}.
     */
    protected RangeValueDouble newDoubleRangeValue() {
        Object first = pop(1);
        Object second = pop();
        Double firstDouble = null;
        Double secondDouble = null;
        if (first instanceof Double) {
            firstDouble = (Double) first;
        } else if (first instanceof Long) {
            firstDouble = ((Long) first).doubleValue();
        } else {
            throw new RuntimeException("Unknown instance type: " + first.getClass().getName());
        }
        if (second instanceof Double) {
            secondDouble = (Double) second;
        } else if (second instanceof Long) {
            secondDouble = ((Long) second).doubleValue();
        } else {
            throw new RuntimeException("Unknown instance type: " + first.getClass().getName());
        }
        return new RangeValueDouble(firstDouble, secondDouble);
    }

    /**
     * Trims off ' and " characters from beginning and end of the string.
     *
     * @param s String to be trimmed off.
     * @return Trimmed off string.
     */
    protected String trimOffEnds(String s) {
        return s.substring(1, s.length() - 1);
    }

    /**
     * Returns or creates new value proxy for given name.
     *
     * @param name Name of the value proxy.
     * @return Proxy value.
     */
    protected Value<?> getValueProxy(String name) {
        String parent = parentName;
        while (parent != null) {
            String testName = null;
            if (parent.isEmpty()) {
                testName = name;
                parent = null;
            } else {
                testName = parent + "." + name;
                parent = stripOffLastReference(parent);
            }
            if (proxyValues.containsKey(testName)) {
                return proxyValues.get(testName);
            }
        }
        throw new InvalidReferenceNameException(name);
    }

    /**
     * Strips off the last reference from name.
     *
     * @param name Name from which to strip off the last reference.
     * @return Name with stripped off last reference.
     */
    protected String stripOffLastReference(String name) {
        if (!name.contains(".")) {
            return "";
        } else {
            return name.substring(0, name.lastIndexOf('.'));
        }
    }

    /**
     * Constructs {@link StringTransformer}.
     *
     * @return Instance of {@link StringTransformer}.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected Value<String> getToStringValue() {
        List values = getValuesUpToDelimiter(STRING_VALUE_DELIMITER);
        String formatString = (String) pop();
        return new StringTransformer(formatString, values);
    }

    /**
     * Collects all values up to specified delimiter.
     *
     * @param delimiter Delimiter up to which to collect all the values.
     * @param <T> Type value would evaluate to.
     * @return List of values up to specified delimiter.
     */
    @SuppressWarnings({ "unchecked" })
    protected <T> List<Value<T>> getValuesUpToDelimiter(String delimiter) {
        List<Value<T>> result = new ArrayList<>();
        while (true) {
            Object val = pop();
            if (val instanceof String && ((String) val).equals(delimiter)) {
                break;
            } else {
                result.add((Value<T>) val);
            }
        }
        Collections.reverse(result);
        return result;
    }
}
