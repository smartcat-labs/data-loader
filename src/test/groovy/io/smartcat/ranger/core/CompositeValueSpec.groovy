package io.smartcat.ranger.core

import spock.lang.Specification

class CompositeValueSpec extends BaseValueSpec {

    def "get should not evaluate value after first call"() {
        given:
        CompositeValue compositeValue = new CompositeValue([a:circ([10, 11]),b:circ(["value1", "value2"])])
        compositeValue.get()

        expect:
        compositeValue.get() == [a:10, b:"value1"]
    }

    def "get should return next values when value is reseted"() {
        given:
        CompositeValue compositeValue = new CompositeValue([a:circ([10, 11, 12, 13]),
            b:circ(["value1", "value2", "value3", "value4"])])
        compositeValue.get()

        when:
        compositeValue.reset()
        compositeValue.reset()
        compositeValue.reset()
        compositeValue.reset()

        then:
        compositeValue.get() == [a:11, b:"value2"]
    }
}
