package io.smartcat.ranger.core

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeValueDoubleSpec extends Specification {

    def "cannot create range value when beginning is greater than the end"() {
        when:
        new RangeValueDouble(new Range<Double>(51.2d, 10.01d))

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        def dist = Mock(Distribution) {
            nextDouble(11.2, 123.3) >> 28.5
        }
        def value = new RangeValueDouble(new Range<Double>(11.2d, 123.3d), false, dist)

        when:
        def result = value.get()

        then:
        result == 28.5
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def dist = Mock(Distribution) {
            nextDouble(11.2, 123.3) >>> 28.5 >> 31.5 >> 18.2 >> 20
        }
        def value = new RangeValueDouble(new Range<Double>(11.2d, 123.3d), false, dist)
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == 28.5 }
    }

    def "multiple reset should not change state"() {
        given:
        def dist = Mock(Distribution) {
            nextDouble(11.2, 123.3) >>> 28.5 >> 31.5 >> 18.2 >> 20
        }
        def value = new RangeValueDouble(new Range<Double>(11.2d, 123.3d), false, dist)
        value.get()

        when:
        10.times { value.reset() }

        then:
        value.get() == 31.5 
    }

    def "should return edge cases when edge cases are turned on"() {
        given:
        def beginning = 11.2d
        def end = 123.3d
        def val1 = 28.5d
        def val2 = 31.5d
        def val3 = 18.2d
        def dist = Mock(Distribution) {
            nextDouble(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueDouble(new Range<Date>(beginning, end), true, dist)

        when:
        def result1 = value.get()
        value.reset()

        then:
        result1 == beginning

        when:
        def result2 = value.get()
        value.reset()

        then:
        result2 == end - RangeValueDouble.EPSILON

        when:
        def result3 = value.get()

        then:
        result3 == val1
    }
}
