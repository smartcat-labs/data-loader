package io.smartcat.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Lists;


@Document(collection = "users")
public class User {
	
	private String username;
	private String firstname;
	private String lastname;
	private Date birthDate;
	
	private Long numberOfCards;
	
	private Double accountBalance;
	
	private final List<String> favoriteMovies = Lists.newArrayList();
	
	private Address address;
	
	// getters and setters
	
	@Override
	public String toString() {
		return "User [username=" + username + ", firstname=" + firstname + ", lastname=" + lastname + ", birthDate="
				+ birthDate + ", numberOfCards=" + numberOfCards + ", accountBalance=" + accountBalance
				+ ", favoriteMovies=" + favoriteMovies + ", address=" + address + "]";
	}
	public String getUsername() {
		return username;
	}
	public Double getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(Double accountBalance) {
		this.accountBalance = accountBalance;
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
	public long getNumberOfCards() {
		return numberOfCards;
	}
	public void setNumberOfCards(long numberOfCards) {
		this.numberOfCards = numberOfCards;
	}
	public List<String> getFavoriteMovies() {
		return favoriteMovies;
	}
	public void setFavoriteMovies(Collection<String> favoriteMovies) {
		this.favoriteMovies.clear();
		if (favoriteMovies != null) {
			this.favoriteMovies.addAll(favoriteMovies);
		}
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}

	public void setNumberOfCards(Long numberOfCards) {
		this.numberOfCards = numberOfCards;
	}
	
}
