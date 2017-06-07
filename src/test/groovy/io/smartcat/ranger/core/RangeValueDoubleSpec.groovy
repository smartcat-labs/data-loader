package io.smartcat.ranger.core

import org.hamcrest.core.Every

import io.smartcat.ranger.distribution.UniformDistribution
import spock.lang.Specification
import spock.lang.Unroll

class RangeValueDoubleSpec extends RangeValueSpec {

    @Override
    def createValue(beginning, end) {
        new RangeValueDouble(beginning, end)
    }
}
