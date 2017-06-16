package io.smartcat.ranger.core

import java.time.ZoneId

import javax.security.auth.Subject.SecureSet

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeValueDateSpec extends Specification {

    def "cannot create range value when beginning is greater than the end"() {
        given:
        // 2017-01-01 12:00:00 UTC
        def beginning = 1483272000000
        // 2016-01-01 12:00:00 UTC
        def end = 1451649600000

        when:
        new RangeValueDate(new Range<Date>(new Date(beginning), new Date(end)))

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        // 2016-01-01 12:00:00 UTC
        def beginning = 1451649600000
        // 2017-01-01 12:00:00 UTC
        def end = 1483272000000
        // 2016-05-01 12:00:00 UTC
        def val = 1462100400000
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >> val
        }
        def value = new RangeValueDate(new Range<Date>(new Date(beginning), new Date(end)), false, dist)

        when:
        def result = value.get()

        then:
        result.getTime() == val
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        // 2016-01-01 12:00:00 UTC
        def beginning = 1451649600000
        // 2017-01-01 12:00:00 UTC
        def end = 1483272000000
        // 2016-05-01 12:00:00 UTC
        def val1 = 1462100400000
        // 2016-05-01 12:00:01 UTC
        def val2 = 1462100401000
        // 2016-05-01 12:00:02 UTC
        def val3 = 1462100402000
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueDate(new Range<Date>(new Date(beginning), new Date(end)), false, dist)
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it.getTime() == val1 }
    }

    def "multiple reset should not change state"() {
        given:
        // 2016-01-01 12:00:00 UTC
        def beginning = 1451649600000
        // 2017-01-01 12:00:00 UTC
        def end = 1483272000000
        // 2016-05-01 12:00:00 UTC
        def val1 = 1462100400000
        // 2016-05-01 12:00:01 UTC
        def val2 = 1462100401000
        // 2016-05-01 12:00:02 UTC
        def val3 = 1462100402000
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueDate(new Range<Date>(new Date(beginning), new Date(end)), false, dist)
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result.getTime() == val2
    }

    def "should return edge cases when edge cases are turned on"() {
        given:
        // 2016-01-01 12:00:00 UTC
        def beginning = 1451649600000
        // 2017-01-01 12:00:00 UTC
        def end = 1483272000000
        // 2016-05-01 12:00:00 UTC
        def val1 = 1462100400000
        // 2016-05-01 12:00:01 UTC
        def val2 = 1462100401000
        // 2016-05-01 12:00:02 UTC
        def val3 = 1462100402000
        def dist = Mock(Distribution) {
            nextLong(beginning, end) >>> val1 >> val2 >> val3
        }
        def value = new RangeValueDate(new Range<Date>(new Date(beginning), new Date(end)), true, dist)

        when:
        def result1 = value.get()
        value.reset()

        then:
        result1.getTime() == beginning

        when:
        def result2 = value.get()
        value.reset()

        then:
        result2.getTime() == end - 1

        when:
        def result3 = value.get()

        then:
        result3.getTime() == val1
    }
}
