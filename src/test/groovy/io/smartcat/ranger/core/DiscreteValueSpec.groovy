package io.smartcat.ranger.core

import io.smartcat.ranger.distribution.Distribution
import spock.lang.Specification

class DiscreteValueSpec extends BaseValueSpec {

    Distribution dist

    def setup() {
        dist = Mock()
    }

    def "value should be taken from list based on index geenrated by distribution"() {
        given:
        dist.nextInt(5) >>> 2 >> 2 >> 3 >> 0
        def value = new DiscreteValue([val(0), val(1), val(2), val(3), val(4)], dist)
        def result = []

        when:
        6.times { result << value.get(); value.reset() }

        then:
        result == [2, 2, 3, 0, 0, 0]
    }

    def "every child value should be reseted but only next value is calculated"() {
        given:
        dist.nextInt(5) >>> 2 >> 2 >> 3 >> 0
        def value = new DiscreteValue([circ([1, 10, 100]), circ([2, 20, 200]), circ([3, 30, 300]), circ([4, 40, 400]),
            circ([5, 50, 500])], dist)

        when:
        def result = value.get()

        then:
        result == 3

        when:
        value.reset()
        result = value.get()

        then:
        result == 30

        when:
        value.reset()
        result = value.get()

        then:
        result == 4

        when:
        value.reset()
        result = value.get()

        then:
        result == 1
    }
}
