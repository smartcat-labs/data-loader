package io.smartcat.ranger.core

import spock.lang.Specification

class StringTransformerSpec extends BaseValueSpec {

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new StringTransformer("a={}, b={}, c: {}", [circ([1, 2, 3]), circ([4, 5, 6]), circ([7, 8, 9])])
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == "a=1, b=4, c: 7" }
    }

    def "multiple reset should not change state"() {
        given:
        def value = new StringTransformer("a={}, b={}, c: {}", [circ([1, 2, 3]), circ([4, 5, 6]), circ([7, 8, 9])])
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result == "a=2, b=5, c: 8"
    }
}
