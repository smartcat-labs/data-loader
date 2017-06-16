package io.smartcat.ranger.core

import java.time.LocalDate

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeValueLocalDateSpec extends Specification {

    def "cannot create range value when beginning is greater than the end"() {
        given:
        // 2017-01-01
        def beginning = 17167
        // 2016-01-01
        def end = 16801

        when:
        new RangeValueLocalDate(LocalDate.ofEpochDay(beginning), LocalDate.ofEpochDay(end))

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        // 2016-01-01
        def beginning = 16801
        // 2017-01-01
        def end = 17167
        // 2016-01-10
        def val = 16810
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >> val
        }
        def value = new RangeValueLocalDate(LocalDate.ofEpochDay(beginning), LocalDate.ofEpochDay(end), false, dist)

        when:
        def result = value.get()

        then:
        result.toEpochDay() == val
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        // 2016-01-01
        def beginning = 16801
        // 2017-01-01
        def end = 17167
        // 2016-01-10
        def val1 = 16810
        // 2016-01-20
        def val2 = 16820
        // 2016-01-25
        def val3 = 16825
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDate(LocalDate.ofEpochDay(beginning), LocalDate.ofEpochDay(end), false, dist)
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it.toEpochDay() == val1 }
    }

    def "multiple reset should not change state"() {
        given:
        // 2016-01-01
        def beginning = 16801
        // 2017-01-01
        def end = 17167
        // 2016-01-10
        def val1 = 16810
        // 2016-01-20
        def val2 = 16820
        // 2016-01-25
        def val3 = 16825
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDate(LocalDate.ofEpochDay(beginning), LocalDate.ofEpochDay(end), false, dist)
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result.toEpochDay() == val2
    }

    def "should return edge cases when edge cases are turned on"() {
        given:
        // 2016-01-01
        def beginning = 16801
        // 2017-01-01
        def end = 17167
        // 2016-01-10
        def val1 = 16810
        // 2016-01-20
        def val2 = 16820
        // 2016-01-25
        def val3 = 16825
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDate(LocalDate.ofEpochDay(beginning), LocalDate.ofEpochDay(end), true, dist)

        when:
        def result1 = value.get()
        value.reset()

        then:
        result1.toEpochDay() == beginning

        when:
        def result2 = value.get()
        value.reset()

        then:
        result2.toEpochDay() == end - 1

        when:
        def result3 = value.get()

        then:
        result3.toEpochDay() == val1
    }
}
