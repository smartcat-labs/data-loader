package io.smartcat.model;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.smartcat.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserTest {

    @Autowired
    private UserRepository userRepository;

    @Before
    public void prepare() {
        userRepository.deleteAll();
    }

    @Test
    public void test() {
        User user = new User();
        user.setUsername("newuser");
        User u = userRepository.save(user);

        long numberOfSavedUsers = userRepository.count();
        Assert.assertEquals(1L, numberOfSavedUsers);
    }

    @Test
    public void randomBuilderTest() {
        RandomUserBuilder randomUserBuilder = new RandomUserBuilder();
        long numberOfUsers = 50;

        LocalDateTime oldestBirthDate = LocalDateTime.of(1950, 1, 1, 0, 0);
        LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1, 0, 0);

        long yesterday = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        long yesterdayPlus1Hour = LocalDateTime.now().minusDays(1).plusHours(1).toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        RandomAddressBuilder addressBuilder = new RandomAddressBuilder();
        addressBuilder
                .randomCityFrom("New York", "Moscow", "London", "Paris", "Budapest", "Las Vegas", "Ulm", "Berlin",
                        "Madrid")
                .randomHouseNumberRange(0, 50)
                .randomStreetFrom("snake street", "nuts str", "cat street", "strawberry street");

        List<User> users = randomUserBuilder
                .randomUsernameFrom("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac",
                        "hardy")
                .randomBirthDateBetween(oldestBirthDate, yougnestBirthDate)
                .randomFavoriteMoviesFrom("Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya") // set random
                                                                                                        // subset
                .randomFirstNameFrom("Alice", "Bob", "Charlie", "Dick", "Eve", "Eleanor", "Boaty")
                .randomLastNameFrom("Avalanche", "Bizmark", "Kok", "McBoatface").randomNumberOfCardsBetween(0, 5)
                .withAddressBuilder(addressBuilder).build(numberOfUsers);

        userRepository.save(users);
        long numberOfSavedUsers = userRepository.count();
        Assert.assertEquals(50L, numberOfSavedUsers);

    }

    @Test
    public void testAggregationQuery() {
        // find 5 most favorite movies for users born between 1985 and 1990, sort by movie favoritness desc;
        // i.e. movies that appear most time in favorites for selected group of users.

        // how to solve this?
        // create a bunch of users, out of which exactly 10 will have some specific properties
        // exactly 10 will be born in range [1985, 1990) and will have specific movies in the list.
        // this list of 10 users is the expected result.
        // all other randomly created users will not have all the specific properties.
        // e.g. some will be born in the range, but will not have correct username
        // e.g. some will have the same username, but will not be in the range etc. Does this have value? Are we just
        // making tautology tests?

        // library ensures that edge cases are created. i.e. E.g. there will be a similar (exactly the same?) person
        // born on 1990-01-01, to protect from off by one error
        // you can then use the specific list of users in various test scenarios

        RandomUserBuilder randomUserBuilder = new RandomUserBuilder();
        long numberOfUsers = 50;

        LocalDateTime oldestBirthDate = LocalDateTime.of(1950, 1, 1, 0, 0);
        LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1, 0, 0);

        long yesterday = LocalDateTime.now().minusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        long yesterdayPlus1Hour = LocalDateTime.now().minusDays(1).plusHours(1).toInstant(ZoneOffset.UTC)
                .toEpochMilli();

        RandomAddressBuilder addressBuilder = new RandomAddressBuilder();
        addressBuilder
                .randomCityFrom("New York", "Moscow", "London", "Paris", "Budapest", "Las Vegas", "Ulm", "Berlin",
                        "Madrid")
                .randomHouseNumberRange(0, 50)
                .randomStreetFrom("snake street", "nuts str", "cat street", "strawberry street");

        List<User> users = randomUserBuilder
                .randomUsernameFrom("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac",
                        "hardy")
                .randomBirthDateBetween(oldestBirthDate, yougnestBirthDate)
                .randomFavoriteMoviesFrom("Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya") // set random
                                                                                                        // subset
                .randomFirstNameFrom("Alice", "Bob", "Charlie", "Dick", "Eve", "Eleanor", "Boaty")
                .randomLastNameFrom("Avalanche", "Bizmark", "Kok", "McBoatface").randomNumberOfCardsBetween(0, 5)
                .withAddressBuilder(addressBuilder) // builder for nested objects
                // .withExactBuilder(exactUserBuilder) // builder for
                .build(numberOfUsers); // creates 50 users based on randomUserBuilder and exactly 10 users based on
                                       // exactUserBuilder where uniqeRules from exactUserBuilder
        //

        userRepository.save(users);
        long numberOfSavedUsers = userRepository.count();
        Assert.assertEquals(50L, numberOfSavedUsers);

        // create exact users

        LocalDateTime newOldestBirthDate = LocalDateTime.of(1985, 1, 1, 0, 0);
        LocalDateTime newYougnestBirthDate = LocalDateTime.of(1990, 1, 1, 0, 0);
        // exclusive rule - rule in a builder that states that only users built with this builder can satisfy that rule.
        RandomUserBuilder exactUserBuilder = new RandomUserBuilder();
        List<User> exactUsers = exactUserBuilder
                .randomUsernameFrom("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac",
                        "hardy")
                .randomBirthDateBetween(newOldestBirthDate, newYougnestBirthDate) // .exclusiveRule() // means that when
                                                                                  // combined with other builders no
                                                                                  // users built by other builders can
                                                                                  // satisfy this rules, i.e. only this
                                                                                  // builder can build users in that
                                                                                  // range of birthdates
                // rule -
                // type : randomFromRangeRule [a,b), randomFromSet
                // exclusive: boolean,
                //
                // .oneWith(favoiteMovies("Predator", "Terminator", "LotR"))
                // .oneWith(favoriteMovies("LotR", "Pirates))
                // .oneWith(favoriteMovies("Matrix", "Lotr", "Fight Club"))
                // .oneWith(favoriteMovies(Matrix", "Terminator"))
                // .oneWith(favoriteMovies("Conan"))
                .build(5);
        //
        // exactUsers.get(0).setFavoriteMovies(Lists.newArrayList("Predator", "Terminator", "LotR"));
        // exactUsers.get(1).setFavoriteMovies(Lists.newArrayList("LotR", "Pirates"));
        // exactUsers.get(2).setFavoriteMovies(Lists.newArrayList("Matrix", "Lotr", "Fight Club"));
        // exactUsers.get(3).setFavoriteMovies(Lists.newArrayList("Matrix", "Terminator"));
        // exactUsers.get(4).setFavoriteMovies(Lists.newArrayList("Conan"));

        // BuildRunner.with(randomUserBuilder).with(exactUserBuilder).build(50);
    }

    @Test
    public void buildRunnerTest() {
        RandomBuilder randomUserBuilder = new RandomBuilder();

        LocalDateTime oldestBirthDate = LocalDateTime.of(1975, 1, 1, 0, 0);
        LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1, 0, 0);

        randomUserBuilder
                .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac",
                        "pevac")
                .randomFrom("firstname", "john", "alice", "bob", "charlie", "david", "elon")
                .randomFrom("lastname", "annison", "berkley", "chaplin", "dickinson")
                .randomFromRange("numberOfCards", 0L, 14L).randomFromRange("accountBalance", 2.72, 3.14)
                .randomSubsetFrom("favoriteMovies", "Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya")
                .randomFromRange("birthDate", oldestBirthDate, yougnestBirthDate).toBeBuilt(5);

        RandomBuilder exclusiveUserBuilder = new RandomBuilder();

        LocalDateTime oldestBirthDateX = LocalDateTime.of(1985, 1, 1, 0, 0);
        LocalDateTime yougnestBirthDateX = LocalDateTime.of(1985, 1, 2, 0, 0);

        exclusiveUserBuilder
                .exclusiveRandomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero")
                .randomFrom("firstname", "john", "alice", "bob", "charlie", "david", "elon")
                .randomFrom("lastname", "annison", "berkley", "chaplin", "dickinson")
                .randomFromRange("numberOfCards", 0L, 14L).randomFromRange("accountBalance", 2.72, 3.14)
                .randomSubsetFrom("favoriteMovies", "Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya")
                .exclusiveRandomFromRange("birthDate", oldestBirthDateX, yougnestBirthDateX).toBeBuilt(3);

        BuildRunner runner = new BuildRunner();

        runner.addBuilder(randomUserBuilder);
        runner.addBuilder(exclusiveUserBuilder);

        List<User> userList = runner.build();

        userList.stream().forEach(System.out::println);

        Assert.assertEquals(8, userList.size());

    }

}
