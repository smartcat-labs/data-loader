package io.smartcat.ranger.core

import spock.lang.Specification

class BaseValueSpec extends Specification {

    def circ(primitiveValues) {
        def values = []
        primitiveValues.each { values << val(it) }
        new CircularValue(values)
    }

    def val(value) {
        PrimitiveValue.of(value)
    }
}
