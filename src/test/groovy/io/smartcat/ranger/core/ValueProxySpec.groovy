package io.smartcat.ranger.core

import io.smartcat.ranger.core.ValueProxy.DelegateNotSetException
import spock.lang.Unroll

class ValueProxySpec extends BaseValueSpec {

    @Unroll
    def "should throw DelegateNotSetException when #method on empty proxy is attempted"() {
        given:
        def value = new ValueProxy()

        when:
        value."$method"()

        then:
        thrown(DelegateNotSetException)

        where:
        method  | _
        "get"   | _
        "reset" | _
    }

    def "calling get multiple times without reset should return same value"() {
        given:
        def value = new ValueProxy()
        value.setDelegate(circ([1, 2, 3, 4]))
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == 1 }
    }

    def "multiple reset should not change state"() {
        given:
        def value = new ValueProxy()
        value.setDelegate(circ([1, 2, 3, 4]))
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result == 2
    }
}
