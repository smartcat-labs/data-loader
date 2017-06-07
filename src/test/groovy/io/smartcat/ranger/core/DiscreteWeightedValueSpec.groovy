package io.smartcat.ranger.core

import io.smartcat.ranger.core.DiscreteWeightedValue.WeightedValue
import spock.lang.Unroll

class DiscreteWeightedValueSpec extends BaseValueSpec {

    def "get should not evaluate value after first call"() {
        given:
        def value = new DiscreteWeightedValue([new WeightedValue(val(1), 50), new WeightedValue(val(2), 50)])
        def firstValue = value.get()
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == firstValue }
    }

    @Unroll
    def "distribution should be equal to weights when number of generated objects is #executeNumber"() {
        given:
        def value = new DiscreteWeightedValue([new WeightedValue(val(0), 50),new WeightedValue(val(1), 20),
            new WeightedValue(val(2), 30)])
        def delta = 0.01
        def nums = [0, 0, 0] as int[]

        when:
        for (int i = 0; i < numOfSamples; i++) {
            nums[value.get()]++
            value.reset()
        }

        then:
        equals(1.0 * nums[0] / numOfSamples, 0.5, delta)
        equals(1.0 * nums[1] / numOfSamples, 0.2, delta)
        equals(1.0 * nums[2] / numOfSamples, 0.3, delta)

        where:
        numOfSamples  | _
        100_000       | _
        1_000_000     | _
        10_000_000    | _
    }

    def equals(actual, expected, delta) {
        Math.abs(actual - expected) < delta
    }
}
