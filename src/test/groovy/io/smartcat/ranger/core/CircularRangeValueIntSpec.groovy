package io.smartcat.ranger.core

import spock.lang.Specification

class CircularRangeValueIntSpec extends Specification {

    def "should start from beginning when value exceeds range end"() {
        given:
        def value = new CircularRangeValueInt(new Range(-2, 2), 1)
        def result = []
        def expected = [-2, -1, 0, 1, 2, -2, -1, 0]

        when:
        8.times { result << value.get(); value.reset() }

        then:
        result == expected
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new CircularRangeValueInt(new Range(2, -7), -2)
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == firstResult }
    }

    def "multiple reset should not change state"() {
        given:
        def value = new CircularRangeValueInt(new Range(-5, 150), 25)
        def val = value.get()

        when:
        value.reset()
        value.reset()
        value.reset()

        then:
        value.get() == 20
    }
}
