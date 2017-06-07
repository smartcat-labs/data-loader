package io.smartcat.ranger.core

class TimeFormatTransformerSpec extends BaseValueSpec {

    def "calling get multiple times without reset should return same value"() {
        given:
        // dates in milliseconds: 2017-06-07, 2017-01-01, 2016-12-31
        def value = new TimeFormatTransformer("YYYY-MM-dd", circ([1496815200000, 1483254000000, 1483167600000]))
        def result = []

        when:
        10.times { result << value.get() }

        then:
        result.size() == 10
        result.every { it == "2017-06-07" }
    }

    def "multiple reset should not change state"() {
        given:
        // dates in milliseconds: 2017-06-07, 2017-01-01, 2016-12-31
        def value = new TimeFormatTransformer("dd.MM.YYYY.", circ([1496815200000, 1483254000000, 1483167600000]))
        value.get()

        when:
        10.times { value.reset() }
        def result = value.get()

        then:
        result == "01.01.2017."
    }
}
