package io.smartcat.ranger.core

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeVauleIntSpec extends Specification {

    def "cannot create range value when beginning is greater than the end"() {
        when:
        new RangeValueInt(new Range<Integer>(15, 10))

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        def dist = Mock(Distribution) {
            nextInt(7, 35) >> 15
        }
        def value = new RangeValueInt(new Range<Integer>(7, 35), false, dist)

        when:
        def result = value.get()

        then:
        result == 15
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def dist = Mock(Distribution) {
            nextInt(11, 100) >>> 13 >> 31 >> 18 >> 20
        }
        def value = new RangeValueInt(new Range<Integer>(11, 100), false, dist)
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
            nextInt(11, 100) >>> 13 >> 31 >> 18 >> 20
        }
        def value = new RangeValueInt(new Range<Integer>(11, 100), false, dist)
        value.get()

        when:
        10.times { value.reset() }

        then:
        value.get() == 31
    }

    def "should return edge cases when edge cases are turned on"() {
        given:
        def beginning = 10
        def end = 25
        def val1 = 20
        def val2 = 13
        def val3 = 18
        def dist = Mock(Distribution) {
            nextInt(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueInt(new Range<Integer>(beginning, end), true, dist)

        when:
        def result1 = value.get()
        value.reset()

        then:
        result1 == beginning

        when:
        def result2 = value.get()
        value.reset()

        then:
        result2 == end - 1

        when:
        def result3 = value.get()

        then:
        result3 == val1
    }
}
