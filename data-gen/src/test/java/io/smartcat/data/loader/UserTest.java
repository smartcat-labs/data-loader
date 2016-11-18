package io.smartcat.data.loader;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;

public class UserTest {

    @Test
    public void buildRunnerTest() {
        RandomBuilder randomUserBuilder = new RandomBuilder();

        LocalDateTime oldestBirthDate = LocalDateTime.of(1975, 1, 1, 0, 0);
        LocalDateTime yougnestBirthDate = LocalDateTime.of(2000, 1, 1, 0, 0);

        randomUserBuilder
            .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac", "pevac")
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
