package io.smartcat.ranger.core

import java.awt.Component.BaselineResizeBehavior

import org.hamcrest.core.Every

import spock.lang.Specification

class CircularValueSpec extends BaseValueSpec {

    def "circular value with one element should always return that one element"() {
        given:
        def value = new CircularValue([val(7)])
        def result = []

        when:
        10.times { result << value.get(); value.reset() }

        then:
        result.size() == 10
        result.every { it == 7 }
    }

    def "circular value with multiple elements should return them in round robin manner"() {
        given:
        def value = new CircularValue([val(5), new DiscreteValue([val(7), val(8), val(9), val(10)]), val(1)])
        def result = []

        when:
        10.times { result << value.get(); value.reset() }

        then:
        result[0] == 5
        result[1] in [7, 8, 9, 10]
        result[2] == 1
        result[3] == 5
        result[4] in [7, 8, 9, 10]
        result[5] == 1
        result[6] == 5
        result[7] in [7, 8, 9, 10]
        result[8] == 1
        result[9] == 5
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new CircularValue([new DiscreteValue([val(7), val(8), val(9), val(10)])])
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
        def value = new CircularValue([val(7), val(8), val(9), val(10), val(11), val(12)])
        def val = value.get()

        when:
        value.reset()
        value.reset()
        value.reset()

        then:
        value.get() == 8
    }
}
