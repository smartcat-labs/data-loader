package io.smartcat.ranger.core

import spock.lang.Specification

class CircularRangeValueDoubleSpec extends Specification {

    def "should start from beginning when value exceeds range end"() {
        given:
        def value = new CircularRangeValueDouble(new Range(-2.0d, 2d), 0.5d)
        def result = []
        def expected = [-2.0d, -1.5d, -1.0d, -0.5d, 0d, 0.5d, 1d, 1.5d, 2.0d, -2.0d]

        when:
        10.times { result << value.get(); value.reset() }

        then:
        for (int i = 0; i < 10; i++) {
            Math.abs(result[i] - expected[i]) < 0.00001d
        }
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new CircularRangeValueDouble(new Range(2.0d, -0.5d), -0.5d)
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
        def value = new CircularRangeValueDouble(new Range(2.0d, -0.5d), -0.5d)
        def val = value.get()

        when:
        value.reset()
        value.reset()
        value.reset()

        then:
        Math.abs(value.get() - 1.5d) < 0.00001d
    }
}
