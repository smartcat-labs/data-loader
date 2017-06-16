package io.smartcat.ranger.model

import java.util.Date
import java.util.List
import java.util.Set

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString(includeNames = true)
class User {

    String username
    String firstName
    String lastName
    Date birthDate
    boolean maried
    Long numberOfCards
    Short numberOfShorts
    Integer numberOfInts

    Double accountBalance
    Float balanceInFloat

    List<String> favoriteMovies
    Set<String> nickNames

    Address address
    List<Address> otherAddresses
}
