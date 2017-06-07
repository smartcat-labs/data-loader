package io.smartcat.ranger.core

import spock.lang.Specification
import spock.lang.Unroll

class RangeValueLongSpec extends RangeValueSpec {

    @Override
    def createValue(beginning, end) {
        new RangeValueLong(beginning, end)
    }
}