package io.smartcat.ranger.core

import io.smartcat.ranger.core.ExactWeightedValue.CountValuePair
import io.smartcat.ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException

class ExactWeightedValueSpec extends BaseValueSpec {

     def "calling get multiple times without reset should return same value"() {
        given:
        def value = new ExactWeightedValue([pair(3, 10), pair(7, 50), pair(10, 40)])
        def firstResult = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == firstResult }
    }

    def "should throw exception when called more times than values defined"() {
        given:
        def value = new ExactWeightedValue([pair(3, 10), pair(7, 50), pair(10, 40)])

        when:
        101.times { value.get(); value.reset() }

        then:
        thrown(ExactWeightedValueDepletedException)
    }

    def "should generate exact number of values as defined"() {
        given:
        def value = new ExactWeightedValue([pair(3, 10), pair(7, 50), pair(10, 40)])
        def result = []
        def numOfThrees = 0
        def numOfSevens = 0
        def numOfTens = 0

        when:
        100.times {
            result << value.get()
            if (value.get() == 3) numOfThrees++
            if (value.get() == 7) numOfSevens++
            if (value.get() == 10) numOfTens++
            value.reset()
        }

        then:
        numOfThrees == 10
        numOfSevens == 50
        numOfTens == 40
    }

    def pair(value, count) {
        new CountValuePair(val(value), count)
    }
}
