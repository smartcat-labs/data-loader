package io.smartcat.data.loader;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.assertj.core.util.Lists;
import org.assertj.core.util.Sets;

public class RandomUserBuilder {

    private String[] usernames;
    private String[] firstNames;
    private String[] lastNames;

    private long birthDateStartRange;
    private long birthDateEndRange;

    private long numberOfCardsStartRange;
    private long numberOfCardsEndRange;

    private String[] movies;

    private RandomAddressBuilder addressBuilder;

    public RandomUserBuilder() {

    }

    public RandomUserBuilder randomUsernameFrom(String... usernames) {
        this.usernames = usernames;
        return this;
    }

    public RandomUserBuilder randomFirstNameFrom(String... firstNames) {
        this.firstNames = firstNames;
        return this;
    }

    public RandomUserBuilder randomLastNameFrom(String... lastNames) {
        this.lastNames = lastNames;
        return this;
    }

    public RandomUserBuilder randomBirthDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        this.birthDateStartRange = startDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        this.birthDateEndRange = endDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        return this;
    }

    public RandomUserBuilder randomNumberOfCardsBetween(long startOfRange, long endOfRange) {
        this.numberOfCardsStartRange = startOfRange;
        this.numberOfCardsEndRange = endOfRange;
        return this;
    }

    public RandomUserBuilder randomFavoriteMoviesFrom(String... movies) {
        this.movies = movies;
        return this;
    }

    public RandomUserBuilder withAddressBuilder(RandomAddressBuilder addressBuilder) {
        this.addressBuilder = addressBuilder;
        return this;
    }

    public User build() {
        return buildRandomUser();
    }

    public List<User> build(long numberOfUsersToBuild) {
        List<User> result = Lists.newArrayList();
        for (long i = 1; i <= numberOfUsersToBuild; i++) {
            User randomUser = buildRandomUser();
            result.add(randomUser);
        }

        return result;
    }

    private User buildRandomUser() {
        User user = new User();
        if (usernames != null && usernames.length > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(0, usernames.length);
            user.setUsername(usernames[randomIndex]);
        } else {
            // TODO choose random string from some default string list?
        }

        int randomFirstNameIndex = ThreadLocalRandom.current().nextInt(0, firstNames.length);
        user.setFirstname(firstNames[randomFirstNameIndex]);

        int randomLastNameIndex = ThreadLocalRandom.current().nextInt(0, lastNames.length);
        user.setLastname(lastNames[randomLastNameIndex]);

        long randomDate = ThreadLocalRandom.current().nextLong(birthDateStartRange, birthDateEndRange);
        Instant instant = Instant.ofEpochMilli(randomDate).atZone(ZoneId.systemDefault()).toInstant();
        user.setBirthDate(Date.from(instant));

        long randomNumberOfCards = ThreadLocalRandom.current().nextLong(numberOfCardsStartRange, numberOfCardsEndRange);
        user.setNumberOfCards(randomNumberOfCards);

        int randomNumberOfFavoriteMovies = ThreadLocalRandom.current().nextInt(0, movies.length);
        Set<String> moviesSet = Sets.newHashSet();
        for (int i = 0; i < randomNumberOfFavoriteMovies; i++) {
            int randomIndex = ThreadLocalRandom.current().nextInt(1, movies.length);
            moviesSet.add(movies[randomIndex]);
        }
        user.getFavoriteMovies().addAll(moviesSet);

        user.setAddress(addressBuilder.build());

        return user;
    }

}
