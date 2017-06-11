package io.smartcat.ranger.core

import spock.lang.Specification

class RandomLengthStringValueSpec extends Specification {

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new RandomLengthStringValue(5)
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.every { it == firstResult }
    }

    def "should return alphanumeric strings when only length is specified"() {
        given:
        def value = new RandomLengthStringValue(9)
        def result = []
        def chars = ('a'..'z').collect { it } + ('A'..'Z').collect { it } + ('0'..'9').collect { it }

        when:
        10.times { result << value.get(); value.reset() }

        then:
        result.every { it.every { it in chars } && it.length() == 9 }
    }

    def "should return only digits from 3 to 9 when digit range is specified"() {
        given:
        char c3 = '3'
        char c9 = '9'
        def value = new RandomLengthStringValue(5, [new Range(c3, c9)])
        def chars = ('3'..'9').collect { it }
        def result = []

        when:
        10.times { result << value.get(); value.reset() }

        then:
        result.every { it.every { it in chars } && it.length() == 5 }
    }

    def "should return only digits and upper keys when digits and upper keys are specified"() {
        given:
        char c2 = '2'
        char c5 = '5'
        char cK = 'K'
        char cW = 'W'
        def value = new RandomLengthStringValue(15, [new Range(c2, c5), new Range(cK, cW)])
        def chars = ('2'..'5').collect { it } + ('K'..'W').collect { it }
        def result = []

        when:
        10.times { result << value.get(); value.reset() }

        then:
        result.every { it.every { it in chars } && it.length() == 15 }
    }
}
