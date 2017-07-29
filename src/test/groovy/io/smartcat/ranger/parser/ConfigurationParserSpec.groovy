package io.smartcat.ranger.parser

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import io.smartcat.ranger.core.InvalidRangeBoundsException
import io.smartcat.ranger.core.RangeValueDouble
import io.smartcat.ranger.core.ExactWeightedValue.ExactWeightedValueDepletedException
import io.smartcat.ranger.distribution.NormalDistribution
import io.smartcat.ranger.distribution.UniformDistribution
import io.smartcat.ranger.util.YamlUtils
import spock.lang.IgnoreRest
import spock.lang.Specification
import spock.lang.Unroll

class ConfigurationParserSpec extends Specification {

    def "should throw InvalidReferenceNameException when reference does not point to any existing value"() {
        given:
        def config = '''
values:
  name: $firstName
output: $name
'''
        when:
        def dataGenerator = buildGenerator(config)

        then:
        Exception e = thrown()
        e.cause instanceof InvalidReferenceNameException
    }

    def "should be able to define reference with underscore"() {
        given:
        def config = '''
values:
  some_value1: 10
  _other2_value: $some_value1
output: $_other2_value
'''
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == 10
    }

    def "should use value from closest visible context for given reference"() {
        given:
        def config = '''
values:
  a:
    b: 10
    c:
      b: 11
      c:
        b: 12
        c:
          x: $b
  y: $a.c.c.c.x
output: $y
'''
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == 12
    }

    def "should be possible to define reference before value"() {
        given:
        def config = '''
values:
  a: $b
  b: 5
output: $a
'''
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == 5
    }

    @Unroll
    def "should parse string value when string is #text"() {
        given:
        def config = """
values:
  name: $value
output: \$name
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value           | parsedValue    | text
        "'value 1'"     | "value 1"      | "single quoted"
        '"value 1"'     | "value 1"      | "double quoted"
        "value 1"       | "value 1"      | "naked"
        "'value 1 '"    | "value 1 "     | "single quoted with trailing space"
        '"value 1 "'    | "value 1 "     | "double quoted with trailing space"
        "value 1 "      | "value 1"      | "naked with trailing space"
        "' value 1'"    | " value 1"     | "single quoted with leading space"
        '" value 1"'    | " value 1"     | "double quoted with leading space"
        " value 1"      | "value 1"      | "naked with leading space"
        " val 1 'a ' x" | "val 1 'a ' x" | "naked with nested single quote"
        ' val 1 "a " x' | 'val 1 "a " x' | "naked with nested double quote"
    }

    @Unroll
    def "should parse byte #value"() {
        given:
        def config = """
values:
  name: $value
output: \$name
"""
        def dataGenerator = buildGenerator(config)

        when:
        def val = dataGenerator.next()

        then:
        val instanceof Byte
        val == parsedValue

        where:
        value       | parsedValue
        "byte(12)"  | (byte) 12
        "byte(+5)"  | (byte) 5
        "byte(-35)" | (byte) -35
    }

    @Unroll
    def "should parse short #value"() {
        given:
        def config = """
values:
  name: $value
output: \$name
"""
        def dataGenerator = buildGenerator(config)

        when:
        def val = dataGenerator.next()

        then:
        val instanceof Short
        val == parsedValue

        where:
        value         | parsedValue
        "short(132)"  | (short) 132
        "short(+221)" | (short) 221
        "short(-342)" | (short) -342
    }

    @Unroll
    def "should parse integer #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def val = dataGenerator.next()

        then:
        val instanceof Integer
        val == parsedValue

        where:
        value        | parsedValue
        "21465"      | 21465
        "+84982"     | 84982
        "-72310"     | -72310
        "int(33322)" | 33322
        "int(+8484)" | 8484
        "int( -433)" | -433
    }

    @Unroll
    def "should parse long #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def val = dataGenerator.next()

        then:
        val instanceof Long
        val == parsedValue

        where:
        value          | parsedValue
        "21474836590"  | 21474836590
        "+21474836590" | 21474836590
        "-21474836590" | -21474836590
        "long(2332)"   | 2332
        "long(+32)"    | 32
        "long(-878)"   | -878
    }

    @Unroll
    def "should parse float #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def val = dataGenerator.next()

        then:
        val instanceof Float
        val == parsedValue

        where:
        value                | parsedValue
        "float(323)"         | 323f
        "float(.12345)"      | 0.12345f
        "float(.6789e9)"     | 0.6789E9f
        "float(.6789e+3)"    | 0.6789E3f
        "float(.6789e-4)"    | 0.6789E-4f
        "float(.6789E7)"     | 0.6789E7f
        "float(.6789E+2)"    | 0.6789E2f
        "float(.6789E-5)"    | 0.6789E-5f
        "float(+.12345)"     | 0.12345f
        "float(+.6789e6)"    | 0.6789E6f
        "float(+.6789e+4)"   | 0.6789E4f
        "float(+.6789e-3)"   | 0.6789E-3f
        "float(+.6789E5)"    | 0.6789E5f
        "float(+.6789E+2)"   | 0.6789E2f
        "float(+.6789E-7)"   | 0.6789E-7f
        "float(-.12345)"     | -0.12345f
        "float(-.6789e4)"    | -0.6789E4f
        "float(-.6789e+2)"   | -0.6789E2f
        "float(-.6789e-3)"   | -0.6789E-3f
        "float(-.6789E3)"    | -0.6789E3f
        "float(-.6789E+5)"   | -0.6789E5f
        "float(-.6789E-8)"   | -0.6789E-8f
        "float(54.6789)"     | 54.6789f
        "float(54.6789e5)"   | 54.6789E5f
        "float(54.6789e+6)"  | 54.6789E6f
        "float(54.6789e-7)"  | 54.6789E-7f
        "float(54.6789E8)"   | 54.6789E8f
        "float(54.6789E+2)"  | 54.6789E2f
        "float(54.6789E-5)"  | 54.6789E-5f
        "float(+54.6789)"    | 54.6789f
        "float(+54.6789e3)"  | 54.6789E3f
        "float(+54.6789e+6)" | 54.6789E6f
        "float(+54.6789e-8)" | 54.6789E-8f
        "float(+54.6789E5)"  | 54.6789E5f
        "float(+54.6789E+3)" | 54.6789E3f
        "float(+54.6789E-8)" | 54.6789E-8f
        "float(-54.6789)"    | -54.6789f
        "float(-54.6789e4)"  | -54.6789E4f
        "float(-54.6789e+5)" | -54.6789E+5f
        "float(-54.6789e-7)" | -54.6789E-7f
        "float(-54.6789E2)"  | -54.6789E2f
        "float(-54.6789E+3)" | -54.6789E3f
        "float(-54.6789E-6)" | -54.6789E-6f
    }

    @Unroll
    def "should parse double #value"() {
        given:
        def config = """
values:
  age: $value
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == parsedValue

        where:
        value            | parsedValue
        ".12345"         | 0.12345
        ".6789e9"        | 0.6789E9
        ".6789e+3"       | 0.6789E3
        ".6789e-4"       | 0.6789E-4
        ".6789E7"        | 0.6789E7
        ".6789E+2"       | 0.6789E2
        ".6789E-5"       | 0.6789E-5
        "+.12345"        | 0.12345
        "+.6789e6"       | 0.6789E6
        "+.6789e+4"      | 0.6789E4
        "+.6789e-3"      | 0.6789E-3
        "+.6789E5"       | 0.6789E5
        "+.6789E+2"      | 0.6789E2
        "+.6789E-7"      | 0.6789E-7
        "-.12345"        | -0.12345
        "-.6789e4"       | -0.6789E4
        "-.6789e+2"      | -0.6789E2
        "-.6789e-3"      | -0.6789E-3
        "-.6789E3"       | -0.6789E3
        "-.6789E+5"      | -0.6789E5
        "-.6789E-8"      | -0.6789E-8
        "54.6789"        | 54.6789
        "54.6789e5"      | 54.6789E5
        "54.6789e+6"     | 54.6789E6
        "54.6789e-7"     | 54.6789E-7
        "54.6789E8"      | 54.6789E8
        "54.6789E+2"     | 54.6789E2
        "54.6789E-5"     | 54.6789E-5
        "+54.6789"       | 54.6789
        "+54.6789e3"     | 54.6789E3
        "+54.6789e+6"    | 54.6789E6
        "+54.6789e-8"    | 54.6789E-8
        "+54.6789E5"     | 54.6789E5
        "+54.6789E+3"    | 54.6789E3
        "+54.6789E-8"    | 54.6789E-8
        "-54.6789"       | -54.6789
        "-54.6789e4"     | -54.6789E4
        "-54.6789e+5"    | -54.6789E+5
        "-54.6789e-7"    | -54.6789E-7
        "-54.6789E2"     | -54.6789E2
        "-54.6789E+3"    | -54.6789E3
        "-54.6789E-6"    | -54.6789E-6
        "double(23)"     | 23d
        "double(-.23)"   | -0.23
        "double(2.3)"    | 2.3
        "double(+33.21)" | 33.21
    }

    def "should parse null value"() {
        given:
        def config = """
values:
  age: null()
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        expect:
        dataGenerator.next() == null
    }

    @Unroll
    def "should parse byte range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Byte
        start <= result && result <= end

        where:
        expression              | start        | end
        "byte(-10)..byte(-5)"   | (byte) -10   | (byte) -5
        " byte( -10)..byte(-5)" | (byte) -10   | (byte) -5
        "byte(-10)..byte(0)  "  | (byte) -10   | (byte) 0
        " byte( -10)..byte(5)"  | (byte) -10   | (byte) 5
        "byte(0)..byte(10)"     | (byte) 0     | (byte) 10
        "byte(5)..byte(10)  "   | (byte) 5     | (byte) 10
    }

    @Unroll
    def "should parse short range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Short
        start <= result && result <= end

        where:
        expression                | start         | end
        "short(-10)..short(-5)"   | (short) -10   | (short) -5
        " short( -10)..short(-5)" | (short) -10   | (short) -5
        "short(-10)..short(0)  "  | (short) -10   | (short) 0
        " short( -10)..short(5)"  | (short) -10   | (short) 5
        "short(0)..short(10)"     | (short) 0     | (short) 10
        "short(5)..short(10)  "   | (short) 5     | (short) 10
    }

    @Unroll
    def "should parse int range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Integer
        start <= result && result <= end

        where:
        expression            | start | end
        "-10..-5"             | -10   | -5
        " -10..-5"            | -10   | -5
        "   -10..-5   "       | -10   | -5
        "-10..0  "            | -10   | 0
        "  -10..5"            | -10   | 5
        "0..10"               | 0     | 10
        "5..10  "             | 5     | 10
        "int(-10)..int(-5)"   | -10   | -5
        " int( -10)..int(-5)" | -10   | -5
        "int(-10)..int(0)  "  | -10   | 0
        " int( -10)..int(5)"  | -10   | 5
        "int(0)..int(10)"     | 0     | 10
        "int(5)..int(10)  "   | 5     | 10
    }

    @Unroll
    def "should parse int range value with useEdgeCases set to true #expression"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result1 = dataGenerator.next()

        then:
        result1 == start

        when:
        def result2 = dataGenerator.next()

        then:
        result2 == end - 1

        when:
        def result3 = dataGenerator.next()

        then:
        start <= result3 && result3 <= end

        where:
        expression      | start | end
        "12..58, true"  | 12    | 58
        "-2..100, true" | -2    | 100
    }

    @Unroll
    def "should parse int range value with useEdgeCases set to false #expression"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        start <= result && result <= end

        where:
        expression              | start | end
        "-10..-5 , false"       | -10   | -5
        " -10..-5,false"        | -10   | -5
        "   -10..-5   ,  false" | -10   | -5
        "-10..0  , false"       | -10   | 0
        "  -10..5, false"       | -10   | 5
        "0..10"                 | 0     | 10
        "5..10  ,false"         | 5     | 10
    }

    def "should parse int range value with uniform distribution when specified"() {
        given:
        def config = """
values:
  age: random(-20..10, true, uniform ())
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == UniformDistribution
    }

    def "should parse int range value with default normal distribution when specified"() {
        given:
        def config = """
values:
  age: random(-20..10, false, normal ( ) )
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == NormalDistribution
    }

    def "should parse int range value with specific normal distribution when specified"() {
        given:
        def config = """
values:
  age: random(-20..10, false, normal(2.1, 1.1, .3, 12))
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        def distribution = dataGenerator.value.delegate.distribution
        distribution.class == NormalDistribution
        distribution.lower == 0.3
        distribution.upper == 12
    }

    @Unroll
    def "should parse long range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Long
        start <= result && result <= end

        where:
        expression              | start             | end
        "-10432432432454..-5"   | -10432432432454   | -5
        " -10..565645534534543" | -10               | 565645534534543
        "long(-10)..long(-5)"   | -10               | -5
        " long( -10)..long(-5)" | -10               | -5
        "long(-10)..long(0)  "  | -10               | 0
        " long( -10)..long(5)"  | -10               | 5
        "long(0)..long(10)"     | 0                 | 10
        "long(5)..long(10)  "   | 5                 | 10
    }

    @Unroll
    def "should throw InvalidRangeBoundsException for range #start .. #end"() {
        given:
        def config = """
values:
  age: random($start..$end)
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        Exception e = thrown()
        e.cause instanceof InvalidRangeBoundsException

        where:
        start | end
        -10   | -11
        -10   | -10
        0     | 0
        5     | 2
    }

    @Unroll
    def "should parse float range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Float
        start <= result && result <= end

        where:
        expression                  | start    | end
        "float(-10.3)..float(-5)"   | -10.3f   | -5f
        " float( -10)..float(-5.1)" | -10f     | -5.1f
        "float(-10.1)..float(0)  "  | -10.1f   | 0f
        " float( -10)..float(5.02)" | -10f     | 5.02f
        "float(0.1)..float(10.11)"  | 0.1f     | 10.11f
        "float(5.0)..float(10.2)  " | 5.0f     | 10.1f
    }

    @Unroll
    def "should parse double range #expression value"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Double
        startNum <= result && result <= endNum

        where:
        expression                    | startNum | endNum
        "-10.332..-.023E-10"          | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"        | -10.332  | -0.023E-10
        "-10.332..-.023E-10   "       | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"        | -10.332  | -0.023E-10
        "-5.2E-10..0"                 | -5.2E-10 | 0d
        "10..10.5"                    | 10d      | 10.5
        ".23..2.12e3"                 | 0.23     | 2.12E3
        "double(-10.3)..double(-5)"   | -10.3d   | -5d
        " double( -10)..double(-5.1)" | -10d     | -5.1d
        "double(-10.1)..double(0)  "  | -10.1d   | 0d
        " double( -10)..double(5.02)" | -10d     | 5.02d
        "double(0.1)..double(10.11)"  | 0.1d     | 10.11d
        "double(5.0)..double(10.2)  " | 5.0d     | 10.1d
    }

    @Unroll
    def "should parse double range value with useEdgeCases set to true #expression"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result1 = dataGenerator.next()

        then:
        result1 == start

        when:
        def result2 = dataGenerator.next()

        then:
        result2 == end - RangeValueDouble.EPSILON

        when:
        def result3 = dataGenerator.next()

        then:
        start <= result3 && result3 <= end

        where:
        expression           | start    | end
        "-5.2E-10..0, true " | -5.2E-10 | 0d
        "10..10.5,true"      | 10d      | 10.5
    }

    @Unroll
    def "should parse double range value with useEdgeCases set to false #expression"() {
        given:
        def config = """
values:
  age: random($expression)
output: \$age
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        startNum <= result && result <= endNum

        where:
        expression                      | startNum | endNum
        "-10.332..-.023E-10, false"     | -10.332  | -0.023E-10
        "  -10.332..-.023E-10,false"    | -10.332  | -0.023E-10
        "-10.332..-.023E-10  ,  false " | -10.332  | -0.023E-10
        "  -10.332..-.023E-10 ,false"   | -10.332  | -0.023E-10
        "-5.2E-10..0, false  "          | -5.2E-10 | 0d
        "10..10.5 , false"              | 10d      | 10.5
        ".23..2.12e3, false"            | 0.23     | 2.12E3
    }

    def "should parse double range value with uniform distribution when specified"() {
        given:
        def config = """
values:
  age: random(-10.332..-.023E-10, false, uniform())
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == UniformDistribution
    }

    def "should parse double range value with default normal distribution when specified"() {
        given:
        def config = """
values:
  age: random(-10.332..-.023E-10,true,normal ())
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == NormalDistribution
    }

    def "should parse double range value with specific normal distribution when specified"() {
        given:
        def config = """
values:
  age: random(-10.332..-.023E-10, false, normal(1.1, 2.2, 3.3, 4.4))
output: \$age
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        def distribution = dataGenerator.value.delegate.distribution
        distribution.class == NormalDistribution
        distribution.lower == 3.3
        distribution.upper == 4.4
    }

    @Unroll
    def "should parse discrete value #expression"() {
        given:
        def config = """
values:
  value: random([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result in values

        where:
        expression                 | values
        "  5,6, 7 ,8 , 9   ,  10"  | [5, 6, 7, 8, 9, 10]
        "5.0, 3.4 , +.12, 0.23   " | [5d, 3.4d, 0.12d, 0.23d]
        """"a", 'b' , 'c' ,"d" """ | ["a", "b", "c", "d"]
    }

    def "should parse discrete value with uniform distribution when specified"() {
        given:
        def config = """
values:
  value: random([1, 2, 4], uniform ( ))
output: \$value
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == UniformDistribution
    }

    def "should parse discrete value with default normal distribution when specified"() {
        given:
        def config = """
values:
  value: random([1, 2, 4], normal())
output: \$value
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.value.delegate.distribution.class == NormalDistribution
    }

    def "should parse discrete value with specific normal distribution when specified"() {
        given:
        def config = """
values:
  value: random([1, 2, 4], normal( 2,3.2, 0.5 , 1))
output: \$value
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        def distribution = dataGenerator.value.delegate.distribution
        distribution.class == NormalDistribution
        distribution.lower == 0.5
        distribution.upper == 1
    }

    def "should throw exception when discrete value with normal distribution have wrong num of params"() {
        given:
        def config = """
values:
  value: random([1, 2, 4], normal( 2, 0.5 , 1))
output: \$value
"""
        when:
        def dataGenerator = buildGenerator(config)

        then:
        Exception e = thrown()
        e.cause.class == RuntimeException
    }

    @Unroll
    def "should parse circular value #expression"() {
        given:
        def config = """
values:
  value: circular([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result == values

        where:
        expression                 | values
        "  5,6, 7 ,8 , 9   ,  10"  | [5, 6, 7, 8, 9, 10]
        "5.0, 3.4 , +.12, 0.23   " | [5d, 3.4d, 0.12d, 0.23d]
        """"a", 'b' , 'c' ,"d" """ | ["a", "b", "c", "d"]
    }

    @Unroll
    def "should parse circular range value byte #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []
        def byteValues = []
        values.each { byteValues << (byte) it }

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Byte }
        result == byteValues

        where:
        expression                        | values
        " byte(3)..byte(12 ) , byte(2 )"  | [3, 5, 7, 9, 11, 3, 5]
        " byte(1)..byte(-6),byte(-1)"     | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " byte(0)..byte(100) , byte(10) " | [0, 10, 20, 30, 40, 50]
    }

    @Unroll
    def "should parse circular range value short #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []
        def shortValues = []
        values.each { shortValues << (short) it }

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Short }
        result == shortValues

        where:
        expression                          | values
        " short(3)..short(12) , short(2)"   | [3, 5, 7, 9, 11, 3, 5]
        " short(1)..short(-6),short(-1)"    | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " short(0)..short(100) , short(10)" | [0, 10, 20, 30, 40, 50]
    }

    @Unroll
    def "should parse circular range value int #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Integer }
        result == values

        where:
        expression                    | values
        " 3..12 , 2"                  | [3, 5, 7, 9, 11, 3, 5]
        " 1..-6,-1"                   | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " 0..100 , 10"                | [0, 10, 20, 30, 40, 50]
        " int(3)..int(12) , int(2)"   | [3, 5, 7, 9, 11, 3, 5]
        " int(1)..int(-6),int(-1)"    | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " int(0)..int(100) , int(10)" | [0, 10, 20, 30, 40, 50]
    }

    @Unroll
    def "should parse circular range value long #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []
        def longValues = []
        values.each { longValues << (long) it }

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Long }
        result == longValues

        where:
        expression     | values
        " long(3)..long(12) , long(2)"   | [3, 5, 7, 9, 11, 3, 5]
        " long(1)..long(-6),long(-1)"    | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " long(0)..long(100) , long(10)" | [0, 10, 20, 30, 40, 50]
    }

    @Unroll
    def "should parse circular range value float #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []
        def floatValues = []
        values.each { floatValues << (float) it }

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Float }
        for (int i = 0; i < floatValues.size(); i++) {
            Math.abs(floatValues[i] - result[i]) < 0.001
        }

        where:
        expression                             | values
        "float(2.1)..float(2.55), float(0.05)" | [2.1, 2.15, 2.20, 2.25, 2.30, 2.35, 3.40, 2.45, 2.50, 2.55, 2.1]
        "float(9.5)..float(7), float(-0.5)"    | [9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 9.5]
        "float(2.0)..float(-4.5), float(-1.5)" | [2.0, 0.5, -1.0, -2.5, -4.0, 2.0]
    }

    @Unroll
    def "should parse circular range value double #expression"() {
        given:
        def config = """
values:
  value: circular($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def result = []

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result.every { it instanceof Double }
        for (int i = 0; i < values.size(); i++) {
            Math.abs(values[i] - result[i]) < 0.00001
        }

        where:
        expression                                | values
        "2.1..2.55, 0.05"                         | [2.1, 2.15, 2.20, 2.25, 2.30, 2.35, 3.40, 2.45, 2.50, 2.55, 2.1]
        "9.5..7, -0.5"                            | [9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 9.5]
        "2.0..-4.5, -1.5"                         | [2.0, 0.5, -1.0, -2.5, -4.0, 2.0]
        "double(2.1)..double(2.55), double(0.05)" | [2.1, 2.15, 2.20, 2.25, 2.30, 2.35, 3.40, 2.45, 2.50, 2.55, 2.1]
        "double(9.5)..double(7), double(-0.5)"    | [9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 9.5]
        "double(2.0)..double(-4.5), double(-1.5)" | [2.0, 0.5, -1.0, -2.5, -4.0, 2.0]
    }

    def "should parse list value #expression"() {
        when:
        def config = """
values:
  value: list(["Ema", circular(["Mike", "Steve", "John"]), "Ned", circular(["Jessica", "Lisa"])])
output: \$value
"""
        def dataGenerator = buildGenerator(config)

        then:
        dataGenerator.next() == ["Ema", "Mike", "Ned", "Jessica"]

        then:
        dataGenerator.next() == ["Ema", "Steve", "Ned", "Lisa"]

        then:
        dataGenerator.next() == ["Ema", "John", "Ned", "Jessica"]

        then:
        dataGenerator.next() == ["Ema", "Mike", "Ned", "Lisa"]
    }

    def "should parse now"() {
        given:
        def config = """
values:
  value: now()
output: \$value
"""
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd")
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Long
        formatter.format(new Date(result)) == formatter.format(new Date())
    }

    def "should parse nowDate"() {
        given:
        def config = """
values:
  value: nowDate()
output: \$value
"""
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd")
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result instanceof Date
        formatter.format(result) == formatter.format(new Date())
    }

    def "should parse nowLocalDate"() {
        given:
        def config = """
values:
  value: nowLocalDate()
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        when:
        def result = dataGenerator.next()

        then:
        result instanceof LocalDate
        result.format(formatter) == LocalDate.now().format(formatter)
    }

    def "should parse nowLocalDateTime"() {
        given:
        def config = """
values:
  value: nowLocalDateTime()
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        when:
        def result = dataGenerator.next()

        then:
        result instanceof LocalDateTime
        result.format(formatter) == LocalDateTime.now().format(formatter)
    }

    @Unroll
    def "should parse weighted value #expression"() {
        given:
        def config = """
values:
  value: weighted([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result in values

        where:
        expression                                       | values
        " (2,2.5),(7, 10) , (4 ,3.5), (8, 8)"            | [2, 7, 4, 8]
        "(5.1,5.2), (8.0, 1) ,(3.3, 5), (100.1, 10.2)"   | [5.1d, 8.0d, 3.3d, 100.1d]
        """("a", 2),('b', 3.2) , ("c", 2), ("d", 5.3)""" | ["a", "b", "c", "d"]
    }

    @Unroll
    def "should parse exact weighted value #expression"() {
        given:
        def config = """
values:
  value: exactly([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def totalCount = 0
        values.each { k, v -> totalCount += v }
        def executeMap = [:]

        when:
        totalCount.times { 
            def val = dataGenerator.next()
            executeMap[val] == null ? executeMap[val] = 1 : executeMap[val]++
        }

        then:
        values == executeMap

        where:
        expression                                   | values
        " (2,2),(7, 10) , (4 ,3), (8, 8)"            | [2:2, 7:10, 4:3, 8:8]
        "(5.1,5), (8.0, 1) ,(3.3, 5), (100.1, 10)"   | [5.1d:5, 8.0d:1, 3.3d:5, 100.1d:10]
        """("a", 2),('b', 3) , ("c", 2), ("d", 5)""" | ["a":2, "b":3, "c":2, "d":5]
    }

    @Unroll
    def "should throw exception when called more times than values defined #expression"() {
        given:
        def config = """
values:
  value: exactly([$expression])
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def totalCount = 0
        values.each { k, v -> totalCount += v }
        totalCount++

        when:
        totalCount.times { dataGenerator.next() }

        then:
        thrown(ExactWeightedValueDepletedException)

        where:
        expression                                   | values
        " (2,2),(7, 10) , (4 ,3), (8, 8)"            | [2:2, 7:10, 4:3, 8:8]
        "(5.1,5), (8.0, 1) ,(3.3, 5), (100.1, 10)"   | [5.1d:5, 8.0d:1, 3.3d:5, 100.1d:10]
        """("a", 2),('b', 3) , ("c", 2), ("d", 5)""" | ["a":2, "b":3, "c":2, "d":5]
    }

    @Unroll
    def "should parse random content value #expression"() {
        given:
        def config = """
values:
  value: randomContentString($expression)
output: \$value
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result.length() == length
        result.every { it in values }

        where:
        expression                                                       | length | values
        "4"                                                              | 4      | ('a'..'z').collect { it } + ('A'..'Z').collect { it } + ('0'..'9').collect { it }
        "5, ['3'..'8', 'A'..'C'] "                                       | 5      | ('3'..'8').collect { it } + ('A'..'C').collect { it }
        """6, ['\\''..'.', '.'..';', ','..'/', '"'..'.', '#'..'\\\\']""" | 6      | ('"'..'}').collect { it }
    }

    def "should parse random content value with varable length"() {
        given:
        def config = """
values:
  value: randomContentString(circular(5..10, 1), ['A'..'Z'])
output: \$value
"""
        def dataGenerator = buildGenerator(config)
        def alowedValues = ('A'..'Z').collect { it }

        when:
        def result1 = dataGenerator.next()

        then:
        result1.length() == 5
        result1.every { it in alowedValues }

        when:
        def result2 = dataGenerator.next()

        then:
        result2.length() == 6
        result2.every { it in alowedValues }

        when:
        def result3 = dataGenerator.next()

        then:
        result3.length() == 7
        result3.every { it in alowedValues }
    }

    @Unroll
    def "should parse string transformer #expression"() {
        given:
        def config = """
values:
output: string($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression                                  | value
        "' some Text '"                             | " some Text "
        '   " some Text " '                         | " some Text "
        "' some {} text {}', 'a', 123"              | " some a text 123"
        ' " some{}text {} {}{}", 3.4, 12, "x", "y"' | " some3.4text 12 xy"
    }

    @Unroll
    def "should parse json transformer #expression"() {
        given:
        def config = """
values:
  a:
    w: true
    x: 10
    y: 23.44
    z: "text"
output: json($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression | value
        '$a'       | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        ' $a'      | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        '$a '      | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        ' $a '     | '{"w":true,"x":10,"y":23.44,"z":"text"}'
        '   $a  '  | '{"w":true,"x":10,"y":23.44,"z":"text"}'
    }

    @Unroll
    def "should parse time transformer #expression"() {
        given:
        def config = """
values:
  time: 1496815200000
output: time($expression)
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result == value

        where:
        expression                         | value
        '"YYYY-MM-dd", $time'              | "2017-06-07"
        '\'YYYY-MM-dd\',$time'             | "2017-06-07"
        '  "YYYY"  ,   $time'              | "2017"
        '"MM-dd",$time'                    | "06-07"
        '   \'dd.MM.YYYY.\',$time'         | "07.06.2017."
        '"YYYY-MM-dd", $time'              | "2017-06-07"
        '   "MM/dd/YYYY",$time  '          | "06/07/2017"
        '"yyyy-MM-dd", now()'              | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        '"yyyy-MM-dd", nowDate()'          | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        '"yyyy-MM-dd", nowLocalDate()'     | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
        '"yyyy-MM-dd", nowLocalDateTime()' | new SimpleDateFormat("yyyy-MM-dd").format(new Date())
    }

    def "should parse nested expression"() {
        given:
        def config = '''
values:
  base:
    a: random([ 5,  random(10..15),20])
    b: time("YYYY-MM-dd", 1496815200000)
    c: constant string
  result:
    x: $base.a
    y: $base.b
    z: $base.c
    w: 25
    q: random([null(), ""])

output: $result
'''
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result.x in [5, 10, 11, 12, 13, 14, 15, 20]
        result.y == "2017-06-07"
        result.z == "constant string"
        result.w == 25
        result.q in [null, ""]
    }

    def "should parse config when there are no values and output is integer literal"() {
        given:
        def config = """
values:
output: 3
"""
        def dataGenerator = buildGenerator(config)

        when:
        def result = dataGenerator.next()

        then:
        result  == 3
    }

    def buildGenerator(config) {
        def root = YamlUtils.load(config)
        new ConfigurationParser(root).build()
    }
}
