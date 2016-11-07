package io.smartcat.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.util.Lists;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RandomBuilder {
	
	private Map<String,Rule<?>> fieldRules = Maps.newHashMap();
	
	private int numberOfObjects;
	
	private Map<String, RandomBuilder> nestedObjectBuilderMap = Maps.newHashMap();
	
	public RandomBuilder randomFromRange(String fieldName, LocalDateTime startDate, LocalDateTime endDate) {
		long lower = startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		long upper = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		
		fieldRules.put(fieldName, RangeRule.withRanges(lower, upper));
		
		return this;
	}
	
	public RandomBuilder exclusiveRandomFromRange(String fieldName, LocalDateTime startDate, LocalDateTime endDate) {
		long lower = startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		long upper = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
		
		fieldRules.put(fieldName, RangeRule.withRangesX(lower, upper));
		
		return this;
	}
	
	public RandomBuilder randomFromRange(String fieldName, Long lower, Long upper) {
		fieldRules.put(fieldName, RangeRule.withRanges(lower, upper));
		return this;
	}
	
	public RandomBuilder exclusiveRandomFromRange(String fieldName, Long lower, Long upper) {
		fieldRules.put(fieldName, RangeRule.withRangesX(lower, upper));
		return this;
	}
	
	public RandomBuilder randomFromRange(String fieldName, Double lower, Double upper) {
		fieldRules.put(fieldName, RangeRuleDouble.withRanges(lower, upper));
		return this;
	}
	
	public RandomBuilder exclusiveRandomFromRange(String fieldName, Double lower, Double upper) {
		fieldRules.put(fieldName, RangeRuleDouble.withRangesX(lower, upper));
		return this;
	}
	
	public RandomBuilder randomFrom(String fieldName, String... values) {
		fieldRules.put(fieldName, DiscreteRule.newSet( values));
		return this;
	}
	
	public RandomBuilder randomWithBuilder(String fieldName, RandomBuilder builder) {
		nestedObjectBuilderMap.put(fieldName, builder);
		return this;
	}
	
	public RandomBuilder exclusiveRandomFrom(String fieldName, String... values) {
		fieldRules.put(fieldName, DiscreteRule.newSetExclusive( values));
		return this;
	}
	
	public RandomBuilder randomSubsetFrom(String fieldName, String...values ) {
		
		fieldRules.put(fieldName, SubSetRule.withValues(Sets.newHashSet(values)));
		
		return this;
	}
	
	public RandomBuilder toBeBuilt(int numberOfObjects) {
		this.numberOfObjects = numberOfObjects;
		return this;
	}
	
	public User build(User user) {
		return null;
	}
	
	public List<User> build(long numberOfUsersToBuild) {
		List<User> result = Lists.newArrayList();
		for (long i = 1; i <= numberOfUsersToBuild; i++) {
			User randomUser = buildRandom();
			result.add(randomUser);
		}
		
		return result;
	}
	
	private User buildRandom() {
		User user = new User();
		
		// TODO handle null pointers
		String randomUsername = (String) fieldRules.get("username").getRandomAllowedValue();
		Long randomBirthDate = (Long) fieldRules.get("birthDate").getRandomAllowedValue();
		String randomFirstName = (String) fieldRules.get("firstname").getRandomAllowedValue();
		String randomLastname = (String) fieldRules.get("lastname").getRandomAllowedValue();
		Long randomNumberOfCards = (Long) fieldRules.get("numberOfCards").getRandomAllowedValue();
		Double randomAccountBalance = (Double) fieldRules.get("accountBalance").getRandomAllowedValue();
		
		
		
		Set<String> randomFavoriteMovies = (Set<String>) fieldRules.get("favoriteMovies").getRandomAllowedValue();
		

		
		Instant instant = Instant.ofEpochMilli(randomBirthDate).atZone(ZoneId.systemDefault()).toInstant();
		
//		Address address = nestedObjectBuilderMap.get("address").build();
		
		user.setUsername(randomUsername);
		user.setBirthDate(Date.from(instant));
		user.setFirstname(randomFirstName);
		user.setLastname(randomLastname);
		user.setNumberOfCards(randomNumberOfCards);
		user.setAccountBalance(randomAccountBalance);
		user.setFavoriteMovies(randomFavoriteMovies);
		
		return user;
	}

	public List<User> buildAll() {
		return this.build(numberOfObjects);
	}

	public Map<String,Rule<?>> getFieldRules() {
		return fieldRules;
	}

	public Map<String, RandomBuilder> getNestedObjectBuilderMap() {
		return nestedObjectBuilderMap;
	}

	public void setNestedObjectBuilderMap(Map<String, RandomBuilder> nestedObjectBuilderMap) {
		this.nestedObjectBuilderMap = nestedObjectBuilderMap;
	}

	
}
