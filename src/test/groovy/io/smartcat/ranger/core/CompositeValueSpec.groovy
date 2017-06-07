package io.smartcat.ranger.core

import spock.lang.Specification

class CompositeValueSpec extends Specification {

    def "get should not evaluate value after first call"() {
        given:
        CompositeValue compositeValue = new CompositeValue([a:value(10, 11), b:value("value1", "value2")])
        compositeValue.get()

        expect:
        compositeValue.get() == [a:10, b:"value1"]
    }

    def "get should return next values when value is reseted"() {
        given:
        CompositeValue compositeValue = new CompositeValue([a:value(10, 11), b:value("value1", "value2")])
        compositeValue.get()

        when:
        compositeValue.reset()

        then:
        compositeValue.get() == [a:11, b:"value2"]
    }

    def value(first, second) {
        new CircularValue<>([PrimitiveValue.of(first), PrimitiveValue.of(second)])
    }
}
