package io.smartcat.data.loader;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class UserTest {

    @Test
    public void should_build_one_user_with_correctly_set_properties() {
        LocalDateTime mayTheFirst = LocalDateTime.of(1975, 5, 1, 0, 0);
        LocalDateTime mayTheSecond = LocalDateTime.of(1975, 5, 2, 0, 0);

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);
        randomUserBuilder.randomFrom("username", "destroyerOfW0rldz")
            .randomFrom("firstname", "alice")
            .randomFrom("lastname", "annison")
            .randomFromRange("numberOfCards", 1L, 2L)
            .randomFromRange("accountBalance", 2.72, 2.73)
            .randomSubsetFrom("favoriteMovies", "Predator")
            .randomFromRange("birthDate", mayTheFirst, mayTheSecond)
            .toBeBuilt(1);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);

        List<User> userList = runner.build();

        userList.stream().forEach(System.out::println);

        User u = userList.get(0);

        Assert.assertEquals("destroyerOfW0rldz", u.getUsername());
        Assert.assertEquals("alice", u.getFirstname());
        Assert.assertEquals("annison", u.getLastname());
        Assert.assertEquals(1L, u.getNumberOfCards());
        Assert.assertTrue(u.getAccountBalance() - 2.72 < 0.1);
        Assert.assertTrue(u.getFavoriteMovies().isEmpty()
                || (u.getFavoriteMovies().size() == 1 && u.getFavoriteMovies().get(0).equals("Predator")));
        Assert.assertTrue("birthdate should be after May 1, 1975",
                u.getBirthDate().compareTo(Date.from(mayTheFirst.toInstant(ZoneOffset.UTC))) > 0);
        Assert.assertTrue("birthdate should be before May 2, 1975",
                u.getBirthDate().compareTo(Date.from(mayTheSecond.toInstant(ZoneOffset.UTC))) < 0);

    }

    // @Test
    // public void buildRunnerTest() {
    // RandomBuilder randomUserBuilder = new RandomBuilder(User.class);
    //
    // LocalDateTime oldestBirthDate = LocalDateTime.of(1975, 1, 1, 0, 0);
    // LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1, 0, 0);
    //
    // randomUserBuilder
    // .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac")
    // .randomFrom("firstname", "john", "alice", "bob", "charlie", "david", "elon")
    // .randomFrom("lastname", "annison", "berkley", "chaplin", "dickinson")
    // .randomFromRange("numberOfCards", 0L, 14L).randomFromRange("accountBalance", 2.72, 3.14)
    // .randomSubsetFrom("favoriteMovies", "Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya")
    //// .randomFromRange("birthDate", oldestBirthDate, yougnestBirthDate)
    // .toBeBuilt(5);
    //
    // RandomBuilder exclusiveUserBuilder = new RandomBuilder(User.class);
    //
    // LocalDateTime oldestBirthDateX = LocalDateTime.of(1985, 1, 1, 0, 0);
    // LocalDateTime yougnestBirthDateX = LocalDateTime.of(1985, 1, 2, 0, 0);
    //
    // exclusiveUserBuilder
    // .exclusiveRandomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero")
    // .randomFrom("firstname", "john", "alice", "bob", "charlie", "david", "elon")
    // .randomFrom("lastname", "annison", "berkley", "chaplin", "dickinson")
    // .randomFromRange("numberOfCards", 0L, 14L).randomFromRange("accountBalance", 2.72, 3.14)
    // .randomSubsetFrom("favoriteMovies", "Predator", "Comandos", "Terminator 2", "Conan", "Red Sonya")
    //// .exclusiveRandomFromRange("birthDate", oldestBirthDateX, yougnestBirthDateX)
    // .toBeBuilt(3);
    //
    // BuildRunner runner = new BuildRunner();
    //
    // runner.addBuilder(randomUserBuilder);
    // runner.addBuilder(exclusiveUserBuilder);
    //
    // List<User> userList = runner.build();
    //
    // userList.stream().forEach(System.out::println);
    //
    // Assert.assertEquals(8, userList.size());
    //
    // }

}
