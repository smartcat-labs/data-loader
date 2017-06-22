package io.smartcat.ranger

import static io.smartcat.ranger.BuilderMethods.*;

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import spock.lang.Specification
import spock.lang.Unroll

class BuilderMethodsSpec extends Specification {

    def "use string"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("a", string("{} has {} {}.", "Josh", 28, "apples")).build();

        when:
        def result = gen.next()

        then:
        result.a == "Josh has 28 apples."
    }

    def "use random"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("username", random("johnsnow12", "_aragorn_", "ragnar5")).build()

        when:
        def result = gen.next()

        then:
        result.username in ["johnsnow12", "_aragorn_", "ragnar5"]
    }

    def "use LocalDate range"() {
        given:
        // 2016-01-01
        def beginning = LocalDate.ofEpochDay(16801)
        // 2017-01-01
        def end = LocalDate.ofEpochDay(17167)
        def gen = new ObjectGeneratorBuilder().prop("graduateDate", random(range(beginning, end))).build()

        when:
        def result = gen.next()

        then:
        result.graduateDate.compareTo(beginning) >= 0
        result.graduateDate.compareTo(end) < 0
    }

    def "use Integer range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range(7, 77))).build()

        when:
        def result = gen.next()

        then:
        result.age >= 7
        result.age < 77
    }

    def "random should throw RuntimeException for unsupported type"() {
        when:
        def gen = new ObjectGeneratorBuilder().prop("stringRange", random(range("aaa", "zzz")))

        then:
        thrown(RuntimeException)
    }

    def "use circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range(1, 2_000_000), 1)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id == 1

        when:
        def result2 = gen.next()

        then:
        result2.id == 2
    }

    def "circular should throw RuntimeException for unsupported range type"() {
        when:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range("aaa", "zzz"), "a")).build()

        then:
        thrown(RuntimeException)
    }

    def "use circular"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("name", circular("firstName", "secondName", "thirdName")).build()

        when:
        def result1 = gen.next()

        then:
        result1.name == "firstName"

        when:
        def result2 = gen.next()

        then:
        result2.name == "secondName"
    }

    def "use random length string without ranges"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("randomString", randomLengthString(5)).build()
        def chars = ('a'..'z').collect { it } + ('A'..'Z').collect { it } + ('0'..'9').collect { it }

        when:
        def result = gen.next()

        then:
        result.randomString.length() == 5
        result.randomString.every { it in chars }
    }

    def "use random length string with ranges"() {
        given:
        char c1 = 'a'
        char c2 = 'f'
        char c3 = '0'
        char c4 = '9'
        def gen = new ObjectGeneratorBuilder()
        .prop("randomString", randomLengthString(10, range(c1, c2), range(c3, c4))).build()
        def chars = ('a'..'z').collect { it } + ('0'..'9').collect { it }

        when:
        def result = gen.next()

        then:
        result.randomString.length() == 10
        result.randomString.every { it in chars }
    } 

    def "use json"() {
        given:
        def gen1 = new ObjectGeneratorBuilder()
        .prop("name", "Peter")
        .prop("lastName", "Smith")
        .prop("age", 25).build()

        def gen2 = new ObjectGeneratorBuilder()
        .prop("output", json(gen1)).build()

        when:
        def result = gen2.next()

        then:
        result.output == """{"name":"Peter","lastName":"Smith","age":25}"""
    }

    def "use time with long"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("examDate", time("YYYY-MM-dd", circular(1493640000000, 1493650000000))).build()

        when:
        def result = gen.next()

        then:
        result.examDate == "2017-05-01"
    }

    @Unroll
    def "use time with #method"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("today", time("yyyy-MM-dd", BuilderMethods."$method"())).build()

        when:
        def result = gen.next()

        then:
        result.today == nowString

        where:
        method             | nowString
        "now"              | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        "nowDate"          | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        "nowLocalDate"     | LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        "nowLocalDateTime" | LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    def "use exactly"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("name", exactly(countPair("Arnold", 2), countPair("Jack", 3), countPair("Peter", 5))).build()

        def numOfArnolds = 0
        def numOfJacks = 0
        def numOfPeters = 0

        when:
        10.times {
            def result = gen.next()
            if (result.name == "Arnold") numOfArnolds++
            if (result.name == "Jack") numOfJacks++
            if (result.name == "Peter") numOfPeters++
        }

        then:
        numOfArnolds == 2
        numOfJacks == 3
        numOfPeters == 5
    }

    def "use weighted"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("name", weighted(weightPair("Arnold", 2), weightPair("Jack", 3), weightPair("Peter", 5))).build()

        def numOfArnolds = 0
        def numOfJacks = 0
        def numOfPeters = 0

        when:
        100.times {
            def result = gen.next()
            if (result.name == "Arnold") numOfArnolds++
            if (result.name == "Jack") numOfJacks++
            if (result.name == "Peter") numOfPeters++
        }

        then:
        numOfArnolds > 0
        numOfJacks > 0
        numOfPeters > 0
    }
}
