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
    def "should parse primitive string value when string is #text"() {
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
    def "should parse primitive integer #value"() {
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
        value    | parsedValue
        "21465"  | 21465
        "+84982" | 84982
        "-72310" | -72310
    }

    @Unroll
    def "should parse primitive long #value"() {
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
        value          | parsedValue
        "21474836590"  | 21474836590
        "+21474836590" | 21474836590
        "-21474836590" | -21474836590
    }

    @Unroll
    def "should parse primitive double #value"() {
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
        value         | parsedValue
        ".12345"      | 0.12345
        ".6789e9"     | 0.6789E9
        ".6789e+3"    | 0.6789E3
        ".6789e-4"    | 0.6789E-4
        ".6789E7"     | 0.6789E7
        ".6789E+2"    | 0.6789E2
        ".6789E-5"    | 0.6789E-5
       "+.12345"      | 0.12345
        "+.6789e6"    | 0.6789E6
        "+.6789e+4"   | 0.6789E4
        "+.6789e-3"   | 0.6789E-3
        "+.6789E5"    | 0.6789E5
        "+.6789E+2"   | 0.6789E2
        "+.6789E-7"   | 0.6789E-7
        "-.12345"     | -0.12345
        "-.6789e4"    | -0.6789E4
        "-.6789e+2"   | -0.6789E2
        "-.6789e-3"   | -0.6789E-3
        "-.6789E3"    | -0.6789E3
        "-.6789E+5"   | -0.6789E5
        "-.6789E-8"   | -0.6789E-8
        "54.6789"     | 54.6789
        "54.6789e5"   | 54.6789E5
        "54.6789e+6"  | 54.6789E6
        "54.6789e-7"  | 54.6789E-7
        "54.6789E8"   | 54.6789E8
        "54.6789E+2"  | 54.6789E2
        "54.6789E-5"  | 54.6789E-5
        "+54.6789"    | 54.6789
        "+54.6789e3"  | 54.6789E3
        "+54.6789e+6" | 54.6789E6
        "+54.6789e-8" | 54.6789E-8
        "+54.6789E5"  | 54.6789E5
        "+54.6789E+3" | 54.6789E3
        "+54.6789E-8" | 54.6789E-8
        "-54.6789"    | -54.6789
        "-54.6789e4"  | -54.6789E4
        "-54.6789e+5" | -54.6789E+5
        "-54.6789e-7" | -54.6789E-7
        "-54.6789E2"  | -54.6789E2
        "-54.6789E+3" | -54.6789E3
        "-54.6789E-6" | -54.6789E-6
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
        start <= result && result <= end

        where:
        expression      | start | end
        "-10..-5"       | -10   | -5
        " -10..-5"      | -10   | -5
        "   -10..-5   " | -10   | -5
        "-10..0  "      | -10   | 0
        "  -10..5"      | -10   | 5
        "0..10"         | 0     | 10
        "5..10  "       | 5     | 10
    }

    @Unroll
    def "should parse long range value with useEdgeCases set to true #expression"() {
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
    def "should parse long range value with useEdgeCases set to false #expression"() {
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

    def "should parse long range value with uniform distribution when specified"() {
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
    
    def "should parse long range value with default normal distribution when specified"() {
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
    
    def "should parse long range value with specific normal distribution when specified"() {
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
        startNum <= result && result <= endNum

        where:
        expression              | startNum | endNum
        "-10.332..-.023E-10"    | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"  | -10.332  | -0.023E-10
        "-10.332..-.023E-10   " | -10.332  | -0.023E-10
        "  -10.332..-.023E-10"  | -10.332  | -0.023E-10
        "-5.2E-10..0"           | -5.2E-10 | 0d
        "10..10.5"              | 10d      | 10.5
        ".23..2.12e3"           | 0.23     | 2.12E3
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
        "  5,6, 7 ,8 , 9   ,  10"  | [5L, 6L, 7L, 8L, 9L, 10L]
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
        "  5,6, 7 ,8 , 9   ,  10"  | [5L, 6L, 7L, 8L, 9L, 10L]
        "5.0, 3.4 , +.12, 0.23   " | [5d, 3.4d, 0.12d, 0.23d]
        """"a", 'b' , 'c' ,"d" """ | ["a", "b", "c", "d"]
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

        when:
        values.size().times { result << dataGenerator.next() }

        then:
        result == values

        where:
        expression     | values
        " 3..12 , 2"   | [3, 5, 7, 9, 11, 3, 5]
        " 1..-6,-1"    | [1, 0, -1, -2, -3, -4, -5, -6, 1, 0, -1]
        " 0..100 , 10" | [0, 10, 20, 30, 40, 50]
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
        for (int i = 0; i < values.size(); i++) {
            Math.abs(values[i] - result[i]) < 0.00001
        }

        where:
        expression        | values
        "2.1..2.55, 0.05" | [2.1, 2.15, 2.20, 2.25, 2.30, 2.35, 3.40, 2.45, 2.50, 2.55, 2.1]
        "9.5..7, -0.5"    | [9.5, 9.0, 8.5, 8.0, 7.5, 7.0, 9.5]
        "2.0..-4.5, -1.5" | [2.0, 0.5, -1.0, -2.5, -4.0, 2.0]
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
        " (2,2.5),(7, 10) , (4 ,3.5), (8, 8)"            | [2L, 7L, 4L, 8L]
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
        " (2,2),(7, 10) , (4 ,3), (8, 8)"            | [2L:2, 7L:10, 4L:3, 8L:8]
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
        " (2,2),(7, 10) , (4 ,3), (8, 8)"            | [2L:2, 7L:10, 4L:3, 8L:8]
        "(5.1,5), (8.0, 1) ,(3.3, 5), (100.1, 10)"   | [5.1d:5, 8.0d:1, 3.3d:5, 100.1d:10]
        """("a", 2),('b', 3) , ("c", 2), ("d", 5)""" | ["a":2, "b":3, "c":2, "d":5]
    }

    @Unroll
    def "should parse random length value #expression"() {
        given:
        def config = """
values:
  value: randomLengthString($expression)
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
        result.x in [5L, 10L, 11L, 12L, 13L, 14L, 15L, 20L]
        result.y == "2017-06-07"
        result.z == "constant string"
        result.w == 25
        result.q in [null, ""]
    }

    def "should parse config when there are no values and output is primitive"() {
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
