package io.smartcat.ranger

import io.smartcat.ranger.model.Address
import io.smartcat.ranger.model.User

import spock.lang.Specification

class ObjectGeneratorBuilderSpec extends Specification {

    def "should be able to pass null to builder"() {
        given:
        def builder = new ObjectGeneratorBuilder()

        when:
        def generator = builder.prop("a", "a").prop("nullProp", null).build()
        def val = generator.next()

        then:
        val.a == "a"
        val.nullProp == null
    }

    def "should create constant value of any type except ObjectGenerator"() {
        given:
        def builder = new ObjectGeneratorBuilder()

        when:
        def generator = builder.prop("a", "a").prop("b", 25).build()
        def val = generator.next()

        then:
        val.a == "a"
        val.b == 25
    }

    def "should extract inner value from ObjectGenerator"() {
        given:
        def gen1 = new ObjectGeneratorBuilder().prop("a", "a").prop("b", 25).build()

        when:
        def generator = new ObjectGeneratorBuilder().prop("c", "c").prop("d", gen1).build()
        def val = generator.next()

        then:
        val.c == "c"
        val.d.a == "a"
        val.d.b == 25
    }

    def "should generate map when no class type is provied"() {
        given:
        def gen1 = new ObjectGeneratorBuilder().prop("a", "a").prop("b", 25).build()

        when:
        def generator = new ObjectGeneratorBuilder().prop("c", "c").prop("d", gen1).build()
        def val = generator.next()

        then:
        val == [c:"c", d:[a:"a", b:25]]
    }

    def "def should try to convert to type when class type is provided"() {
        given:
        // 2017-01-01 12:00:00 UTC
        def firstJanuary2017 = 1483272000000
        def otherAddress1 = new Address(city:"New York", street:"5th Avenue", houseNumber:34)
        def otherAddress2 = new Address(city:"Berlin", street:"Ritter Strasse", houseNumber:13)
        def otherAddress3 = new Address(city:"London", street:"Sun St", houseNumber:324)

        def expected = new User(username:"johnSnow23", firstName:"Mike", lastName:"Ehrmantraut",
            birthDate: new Date(firstJanuary2017), maried:true, numberOfCards: 1234567L,
            numberOfShorts: (short) 23, numberOfInts: 55, accountBalance: 11.0000023d,
            balanceInFloat: 11.0001f, favoriteMovies: ["X", "Y", "z"], nickNames: ["a", "b", "c"],
            address: new Address(city: "San Francisco", street: "California St", houseNumber: 25),
            otherAddresses: [otherAddress1, otherAddress2, otherAddress3])

        def addressGen = new ObjectGeneratorBuilder()
        .prop("city", "San Francisco")
        .prop("street", "California St")
        .prop("houseNumber", 25)
        .build(Address)


        def userGen = new ObjectGeneratorBuilder()
        .prop("username", "johnSnow23")
        .prop("firstName", "Mike")
        .prop("lastName", "Ehrmantraut")
        .prop("birthDate", new Date(firstJanuary2017))
        .prop("maried", true)
        .prop("numberOfCards", 1234567L)
        .prop("numberOfShorts", (short) 23)
        .prop("numberOfInts", 55)
        .prop("accountBalance", 11.0000023d)
        .prop("balanceInFloat", 11.0001f)
        .prop("favoriteMovies", ["X", "Y", "z"])
        .prop("nickNames", ["a", "b", "c"])
        .prop("address", addressGen)
        .prop("otherAddresses", [otherAddress1, otherAddress2, otherAddress3])
        .build(User)

        when:
        def val = userGen.next()

        then:
        val == expected
    }
}
