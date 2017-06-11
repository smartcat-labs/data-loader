package io.smartcat.ranger.core

import spock.lang.Specification

class CircularRangeValueLongSpec extends Specification {

    def "should start from beginning when value exceeds range end"() {
        given:
        def value = new CircularRangeValueLong(new Range(-2L, 2L), 1L)
        def result = []
        def expected = [-2L, -1L, 0L, 1L, 2L, -2L, -1L, 0L]

        when:
        8.times { result << value.get(); value.reset() }

        then:
        result == expected
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new CircularRangeValueLong(new Range(2L, -7L), -2L)
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
        def value = new CircularRangeValueLong(new Range(-5L, 150L), 25L)
        def val = value.get()

        when:
        value.reset()
        value.reset()
        value.reset()

        then:
        value.get() == 20L
    }
}
