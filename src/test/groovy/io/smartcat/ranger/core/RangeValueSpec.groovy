package io.smartcat.ranger.core

import spock.lang.Specification
import spock.lang.Unroll

abstract class RangeValueSpec extends Specification {

    def "cannot create range value when beginning is less than end"() {
        when:
        createValue(15, 10)

        then:
        thrown(InvalidRangeBoundsException)
    }

    @Unroll
    def "should generate uniform values when using uniform distribution for range: #beginning - #end, #numOfSamples"() {
        given:
        def value = createValue(beginning, end)
        def numOfSegments = 10
        def percentagePerSegment = 1.0 / numOfSegments
        def segments = new int[numOfSegments]
        def delta = 0.01

        when:
        for (int i = 0; i < numOfSamples; i++) {
            def val = value.get()
            def calculated = normalize(val, beginning, end, 0, numOfSegments)
            segments[calculated]++
            value.reset()
        }

        then:
        segments.every { equals(1.0 * it / numOfSamples, percentagePerSegment, delta)}

        // test scenarios with 10 million samples commented out because build (pitest task) on free travis fails
        where:
        beginning | end     | numOfSamples
        0         | 1000    | 1_000_000
//        0         | 1000    | 10_000_000
        1000      | 10_000  | 1_000_000
//        1000      | 10_000  | 10_000_000
    }

    abstract def createValue(beginning, end)

    def normalize(double value, double lower, double upper, int normalizationLowerBound, int normalizationUpperBound) {
        def normalizedRange = normalizationUpperBound - normalizationLowerBound;
        def innerRange = upper - lower
        (int) (((value - lower) * normalizedRange) / innerRange) + normalizationLowerBound;
    }

    def equals(actual, expected, delta) {
        Math.abs(actual - expected) < (expected * delta)
    }
}