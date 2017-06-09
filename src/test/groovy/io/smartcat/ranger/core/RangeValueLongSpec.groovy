package io.smartcat.ranger.core

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification
import spock.lang.Unroll

class RangeValueLongSpec extends Specification {

    def "cannot create range value when beginning is less than end"() {
        when:
        new RangeValueLong(15, 10)

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        def dist = Mock(Distribution) {
            nextLong(7, 35) >> 15
        }
        def value = new RangeValueLong(7, 35, dist)

        when:
        def result = value.get()

        then:
        result == 15
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def dist = Mock(Distribution) {
            nextLong(11, 100) >>> 13 >> 31 >> 18 >> 20
        }
        def value = new RangeValueLong(11, 100, dist)
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == 13 }
    }

    def "multiple reset should not change state"() {
        given:
        def dist = Mock(Distribution) {
            nextLong(11, 100) >>> 13 >> 31 >> 18 >> 20
        }
        def value = new RangeValueLong(11, 100, dist)
        value.get()

        when:
        10.times { value.reset() }

        then:
        value.get() == 31
    }
}