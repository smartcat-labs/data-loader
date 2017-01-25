package io.smartcat.data.loader.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User dummy entity.
 */
public class User {

    private String username;
    private String firstname;
    private String lastname;
    private Date birthDate;

    private Long numberOfCards;
    private Short numberOfShorts;
    private Integer numberOfInts;

    private Double accountBalance;
    private Float balanceInFloat;

    private List<String> favoriteMovies;

    private Set<String> nicknames;

    private Address address;

    private List<Address> otherAddresses;

    private boolean maried;

    @Override
    public String toString() {
        return "User [username=" + username + ", firstname=" + firstname + ", lastname=" + lastname + ", birthDate="
                + birthDate + ", numberOfCards=" + numberOfCards + ", numberOfShorts=" + numberOfShorts
                + ", numberOfInts=" + numberOfInts + ", accountBalance=" + accountBalance + ", balanceInFloat="
                + balanceInFloat + ", favoriteMovies=" + favoriteMovies + ", nicknames=" + nicknames + ", address="
                + address + ", otherAddresses=" + otherAddresses + ", maried=" + maried + "]";
    }

    // getters and setters

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Long getNumberOfCards() {
        return numberOfCards;
    }

    public void setNumberOfCards(Long numberOfCards) {
        this.numberOfCards = numberOfCards;
    }

    public Short getNumberOfShorts() {
        return numberOfShorts;
    }

    public void setNumberOfShorts(Short numberOfShorts) {
        this.numberOfShorts = numberOfShorts;
    }

    public Integer getNumberOfInts() {
        return numberOfInts;
    }

    public void setNumberOfInts(Integer numberOfInts) {
        this.numberOfInts = numberOfInts;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Float getBalanceInFloat() {
        return balanceInFloat;
    }

    public void setBalanceInFloat(Float balanceInFloat) {
        this.balanceInFloat = balanceInFloat;
    }

    public List<String> getFavoriteMovies() {
        return favoriteMovies;
    }

    public void setFavoriteMovies(List<String> favoriteMovies) {
        this.favoriteMovies = favoriteMovies;
    }

    public Set<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(Set<String> nicknames) {
        this.nicknames = nicknames;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Address> getOtherAddresses() {
        return otherAddresses;
    }

    public void setOtherAddresses(List<Address> otherAddresses) {
        this.otherAddresses = otherAddresses;
    }

    public boolean isMaried() {
        return maried;
    }

    public void setMaried(boolean maried) {
        this.maried = maried;
    }
}
