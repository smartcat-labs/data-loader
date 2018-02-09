package io.smartcat.ranger

import static io.smartcat.ranger.BuilderMethods.*;

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import spock.lang.IgnoreRest
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

    def "use empty list"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("names", emptyList()).build()

        when:
        def result = gen.next()

        then:
        result.names == []
    }

    def "use empty map"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("names", emptyMap()).build()

        when:
        def result = gen.next()

        then:
        result.names == [:]
    }

    def "use random length list"() {
        given:
        def gen = new ObjectGeneratorBuilder().prop("numbers", list(5, 9, random(range(10, 100)))).build()

        when:
        def result1 = gen.next()

        then:
        result1.numbers.size() >= 5 && result1.numbers.size() <= 9
        result1.numbers.every { it >= 10 && it <= 100 }

        when:
        def result2 = gen.next()

        then:
        result2.numbers.size() >= 5 && result2.numbers.size() <= 9
        result2.numbers.every { it >= 10 && it <= 100 }
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

    @Unroll
    def "use addition with #text"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("number", val).build()

        when:
        def result = gen.next()
        
        then:
        result.number.getClass() == type
        result.number == expected

        where:
        val                                                               | type    | expected | text
        add(Byte, constant((byte) 3), constant((byte) 5))                 | Byte    | 8        | "two byte constants"
        add(Short, constant((short) 10), constant((short) 33))            | Short   | 43       | "two short constants"
        add(Integer, constant(5), constant(10))                           | Integer | 15       | "two integer constants"
        add(Long, constant(20L), constant(15L))                           | Long    | 35L      | "two long constants"
        add(Float, constant(1.5f), constant(3.5f))                        | Float   | 5.0f     | "two float constants"
        add(Double, constant(2.2d), constant(0.3d))                       | Double  | 2.5d     | "two double constants"
        add(Long, constant(10L), add(Integer, constant(5), constant(20))) | Long    | 35L      | "nested add method"
    }

    @Unroll
    def "use subtraction with #text"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("number", val).build()

        when:
        def result = gen.next()
        
        then:
        result.number.getClass() == type
        result.number == expected

        where:
        val                                                                         | type    | expected | text
        subtract(Byte, constant((byte) 5), constant((byte) 5))                      | Byte    | 0        | "two byte constants"
        subtract(Short, constant((short) 40), constant((short) 33))                 | Short   | 7        | "two short constants"
        subtract(Integer, constant(10), constant(5))                                | Integer | 5        | "two integer constants"
        subtract(Long, constant(20L), constant(15L))                                | Long    | 5L       | "two long constants"
        subtract(Float, constant(1.5f), constant(3.5f))                             | Float   | -2.0f    | "two float constants"
        subtract(Double, constant(2.1d), constant(0.3d))                            | Double  | 1.8d     | "two double constants"
        subtract(Long, constant(20L), subtract(Integer, constant(15), constant(3))) | Long    | 8L       | "nested subtract method"
    }

    @Unroll
    def "use multiplication with #text"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("number", val).build()

        when:
        def result = gen.next()
        
        then:
        result.number.getClass() == type
        result.number == expected

        where:
        val                                                                         | type    | expected | text
        multiply(Byte, constant((byte) 5), constant((byte) 5))                      | Byte    | 25       | "two byte constants"
        multiply(Short, constant((short) 4), constant((short) 33))                  | Short   | 132      | "two short constants"
        multiply(Integer, constant(10), constant(5))                                | Integer | 50       | "two integer constants"
        multiply(Long, constant(20L), constant(15L))                                | Long    | 300L     | "two long constants"
        multiply(Float, constant(10.0f), constant(3.5f))                            | Float   | 35f      | "two float constants"
        multiply(Double, constant(2.1d), constant(0.5d))                            | Double  | 1.05d    | "two double constants"
        multiply(Long, constant(20L), multiply(Integer, constant(15), constant(3))) | Long    | 900L     | "nested multiply method"
    }

    @Unroll
    def "use division with #text"() {
        given:
        def gen = new ObjectGeneratorBuilder()
        .prop("number", val).build()

        when:
        def result = gen.next()
        
        then:
        result.number.getClass() == type
        result.number == expected

        where:
        val                                                                     | type    | expected | text
        divide(Byte, constant((byte) 5), constant((byte) 5))                    | Byte    | 1        | "two byte constants"
        divide(Short, constant((short) 40), constant((short) 33))               | Short   | 1        | "two short constants"
        divide(Integer, constant(10), constant(5))                              | Integer | 2        | "two integer constants"
        divide(Long, constant(20L), constant(15L))                              | Long    | 1L       | "two long constants"
        divide(Float, constant(14.0f), constant(3.5f))                          | Float   | 4.0f     | "two float constants"
        divide(Double, constant(21d), constant(0.3d))                           | Double  | 70.0d    | "two double constants"
        divide(Long, constant(20L), divide(Integer, constant(15), constant(3))) | Long    | 4L       | "nested divide method"
    }

    def "use csv value with one argument"() {
        given:
        def gen = csv("src/test/resources/csv/a.csv")

        when:
        def result = gen.next()

        then:
        result.c0 == "John"
        result.c1 == "Smith"
        result.c2 == "555-1331"
        result.c3 == "New York"
        result.c4 == "US"

        when:
        result = gen.next()

        then:
        result.c0 == "Peter"
        result.c1 == "Braun"
        result.c2 == "133-1123"
        result.c3 == "Berlin"
        result.c4 == "DE"

        when:
        result = gen.next()

        then:
        result.c0 == "Jose"
        result.c1 == "Garcia"
        result.c2 == "328-3221"
        result.c3 == "Madrid"
        result.c4 == "ES"
    }

    def "use csv value with two arguments"() {
        given:
        char delimiter = ';'
        def gen = csv("src/test/resources/csv/b.csv", delimiter)

        when:
        def result = gen.next()

        then:
        result.c0 == "John"
        result.c1 == "Smith"
        result.c2 == "555-1331"
        result.c3 == "New York"
        result.c4 == "US"

        when:
        result = gen.next()

        then:
        result.c0 == "Peter"
        result.c1 == "Braun"
        result.c2 == "133-1123"
        result.c3 == "Berlin"
        result.c4 == "DE"

        when:
        result = gen.next()

        then:
        result.c0 == "Jose"
        result.c1 == "Garcia"
        result.c2 == "328-3221"
        result.c3 == "Madrid"
        result.c4 == "ES"
    }

    def "use csv value with all arguments"() {
        given:
        char delimiter = ';'
        Character quoteChar = '"'
        char commentMarker = '#'
        def gen = csv("src/test/resources/csv/c.csv", delimiter, "\\n", false, quoteChar, commentMarker, true, null)

        when:
        def result = gen.next()

        then:
        result.c0 == "John"
        result.c1 == "Smith"
        result.c2 == "555-1331"
        result.c3 == "New York "
        result.c4 == "US"

        when:
        result = gen.next()

        then:
        result.c0 == "null"
        result.c1 == "Braun"
        result.c2 == "133-1123"
        result.c3 == "Berlin"
        result.c4 == "DE"

        when:
        result = gen.next()

        then:
        result.c0 == "Jose"
        result.c1 == "Garcia"
        result.c2 == "328-3221"
        result.c3 == "Madrid"
        result.c4 == "ES"
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
