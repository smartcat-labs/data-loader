package io.smartcat.ranger.core

import java.time.LocalDateTime
import java.time.ZoneOffset

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeValueLocalDateTimeSpec extends Specification {

    def "cannot create range value when beginning is greater than the end"() {
        given:
        // 2017-01-01 12:30:25
        def beginning = 1483273825
        // 2016-01-01 12:30:25
        def end = 1451651425

        when:
        new RangeValueLocalDateTime(LocalDateTime.ofEpochSecond(beginning, 0, ZoneOffset.UTC),
            LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC))

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        // 2016-01-01 12:30:25
        def beginning = 1451651425
        // 2017-01-01 12:30:25
        def end = 1483273825
        // 2016-01-04 12:30:20
        def val = 1451910620
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >> val
        }
        def value = new RangeValueLocalDateTime(LocalDateTime.ofEpochSecond(beginning, 0, ZoneOffset.UTC),
            LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC), false, dist)

        when:
        def result = value.get()

        then:
        result.toEpochSecond(ZoneOffset.UTC) == val
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        // 2016-01-01 12:30:25
        def beginning = 1451651425
        // 2017-01-01 12:30:25
        def end = 1483273825
        // 2016-01-04 12:30:20
        def val1 = 1451910620
        // 2016-01-04 12:30:20
        def val2 = 1451910630
        // 2016-01-04 12:30:20
        def val3 = 1451910640
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDateTime(LocalDateTime.ofEpochSecond(beginning, 0, ZoneOffset.UTC),
            LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC), false, dist)
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it.toEpochSecond(ZoneOffset.UTC) == val1 }
    }

    def "multiple reset should not change state"() {
        given:
        // 2016-01-01 12:30:25
        def beginning = 1451651425
        // 2017-01-01 12:30:25
        def end = 1483273825
        // 2016-01-04 12:30:20
        def val1 = 1451910620
        // 2016-01-04 12:30:20
        def val2 = 1451910630
        // 2016-01-04 12:30:20
        def val3 = 1451910640
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDateTime(LocalDateTime.ofEpochSecond(beginning, 0, ZoneOffset.UTC),
            LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC), false, dist)
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result.toEpochSecond(ZoneOffset.UTC) == val2
    }

    def "should return edge cases when edge cases are turned on"() {
        given:
        // 2016-01-01 12:30:25
        def beginning = 1451651425
        // 2017-01-01 12:30:25
        def end = 1483273825
        // 2016-01-04 12:30:20
        def val1 = 1451910620
        // 2016-01-04 12:30:20
        def val2 = 1451910630
        // 2016-01-04 12:30:20
        def val3 = 1451910640
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueLocalDateTime(LocalDateTime.ofEpochSecond(beginning, 0, ZoneOffset.UTC),
            LocalDateTime.ofEpochSecond(end, 0, ZoneOffset.UTC), true, dist)

        when:
        def result1 = value.get()
        value.reset()

        then:
        result1.toEpochSecond(ZoneOffset.UTC) == beginning

        when:
        def result2 = value.get()
        value.reset()

        then:
        result2.toEpochSecond(ZoneOffset.UTC) == end - 1

        when:
        def result3 = value.get()

        then:
        result3.toEpochSecond(ZoneOffset.UTC) == val1
    }
}
