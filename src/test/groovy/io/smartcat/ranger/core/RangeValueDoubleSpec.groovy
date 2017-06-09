package io.smartcat.ranger.core

import javax.security.auth.Subject.SecureSet

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class RangeValueDoubleSpec extends Specification {

    def "cannot create range value when beginning is less than end"() {
        when:
        new RangeValueDouble(15.2, 10.01)

        then:
        thrown(InvalidRangeBoundsException)
    }

    def "should use distribution's sample method with proper bounds"() {
        given:
        def dist = Mock(Distribution) {
            nextDouble(11.2, 123.3) >> 28.5
        }
        def value = new RangeValueDouble(11.2, 123.3, dist)

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
        def value = new RangeValueDouble(11.2, 123.3, dist)
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
        def value = new RangeValueDouble(11.2, 123.3, dist)
        value.get()

        when:
        10.times { value.reset() }

        then:
        value.get() == 31.5 
    }
}
