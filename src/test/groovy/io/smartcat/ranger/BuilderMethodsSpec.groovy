package io.smartcat.ranger

import static io.smartcat.ranger.BuilderMethods.*;

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.smartcat.ranger.model.Address
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

    def "use Byte range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range((byte) 7, (byte) 77))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Byte
        result.age >= 7
        result.age < 77
    }

    def "use Short range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range((short) 7, (short) 77))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Short
        result.age >= 7
        result.age < 77
    }

    def "use Integer range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range(7, 77))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Integer
        result.age >= 7
        result.age < 77
    }

    def "use Long range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range(7L, 77L))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Long
        result.age >= 7
        result.age < 77
    }

    def "use Float range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range(7.0f, 77.0f))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Float
        result.age >= 7.0f
        result.age < 77.0f
    }

    def "use Double range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("age", random(range(7.0d, 77.0d))).build()

        when:
        def result = gen.next()

        then:
        result.age instanceof Double
        result.age >= 7.0d
        result.age < 77.0d
    }

    def "random should throw RuntimeException for unsupported type"() {
        when:
        def gen = new ObjectGeneratorBuilder().prop("stringRange", random(range("aaa", "zzz")))

        then:
        thrown(RuntimeException)
    }

    def "use Byte circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range((byte) 1, (byte) 25), (byte) 1)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Byte
        result1.id == 1

        when:
        def result2 = gen.next()

        then:
        result2.id == 2
    }

    def "use Short circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range((short) 1, (short) 255), (short) 1)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Short
        result1.id == 1

        when:
        def result2 = gen.next()

        then:
        result2.id == 2
    }

    def "use Integer circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range(1, 2_000_000), 1)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Integer
        result1.id == 1

        when:
        def result2 = gen.next()

        then:
        result2.id == 2
    }

    def "use Long circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range(1L, 2_000_000_000L), 1L)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Long
        result1.id == 1

        when:
        def result2 = gen.next()

        then:
        result2.id == 2
    }

    def "use Float circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range(0.0f, 10_000f), 0.1f)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Float
        result1.id == 0.0f

        when:
        def result2 = gen.next()

        then:
        result2.id == 0.1f
    }

    def "use Double circular range"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("id", circular(range(0.0d, 10_000d), 0.1d)).build()

        when:
        def result1 = gen.next()

        then:
        result1.id instanceof Double
        result1.id == 0.0d

        when:
        def result2 = gen.next()

        then:
        result2.id == 0.1d
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

    def "use list"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("names", list("Peter", "Rodger", circular("Mike", "Steve"))).build()

        when:
        def result1 = gen.next()

        then:
        result1.names == ["Peter", "Rodger", "Mike"]

        when:
        def result2 = gen.next()

        then:
        result2.names == ["Peter", "Rodger", "Steve"]
    }

    def "use random content string without ranges"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("randomString", randomContentString(constant(5))).build()
        def chars = ('a'..'z').collect { it } + ('A'..'Z').collect { it } + ('0'..'9').collect { it }

        when:
        def result = gen.next()

        then:
        result.randomString.length() == 5
        result.randomString.every { it in chars }
    }

    def "use random content string with ranges"() {
        given:
        char c1 = 'a'
        char c2 = 'f'
        char c3 = '0'
        char c4 = '9'
        def gen = new ObjectGeneratorBuilder()
        .prop("randomString", randomContentString(circular(range(10, 15), 1), range(c1, c2), range(c3, c4))).build()
        def chars = ('a'..'z').collect { it } + ('0'..'9').collect { it }

        when:
        def result1 = gen.next()

        then:
        result1.randomString.length() == 10
        result1.randomString.every { it in chars }

        when:
        def result2 = gen.next()

        then:
        result2.randomString.length() == 11
        result2.randomString.every { it in chars }
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
