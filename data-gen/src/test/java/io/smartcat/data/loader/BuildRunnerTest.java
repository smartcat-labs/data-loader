package io.smartcat.data.loader;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class BuildRunnerTest {

    @Test
    public void should_allow_not_setting_a_field_in_one_of_the_builders() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        String[] usernameArray = {"destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero" };
        String[] firstNameArray = {"alice", "bob", "charlie"};

        randomUserBuilder
                .randomFrom("username", usernameArray)
                .randomFrom("firstname", firstNameArray).toBeBuilt(100);

        RandomBuilder<User> userBuilderUsernameOnly = new RandomBuilder<User>(User.class, randomizer);
        userBuilderUsernameOnly
                .randomFrom("username", usernameArray)
                .toBeBuilt(10);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        runner.addBuilder(userBuilderUsernameOnly);

        List<User> builtUsers = runner.build();

        int numberOfUsersWithUsernameAndFirstName = 0;
        int numberOfUsersWithUsernameOnly = 0;

        List<String> usernames = Arrays.asList(usernameArray);
        List<String> firstNames = Arrays.asList(firstNameArray);

        for (User u : builtUsers) {
            Assert.assertTrue(usernames.contains(u.getUsername()));
            if (u.getFirstname() == null) {
                numberOfUsersWithUsernameOnly++;
            } else {
                numberOfUsersWithUsernameAndFirstName++;
                Assert.assertTrue(firstNames.contains(u.getFirstname()));
            }
        }
        Assert.assertEquals(100, numberOfUsersWithUsernameAndFirstName);
        Assert.assertEquals(10, numberOfUsersWithUsernameOnly);
    }

    @Test
    public void should_allow_not_setting_different_fields_in_different_builders() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> builderUsernameAndFirstName = new RandomBuilder<User>(User.class, randomizer);

        String[] usernameArray = {"destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero" };
        String[] firstNameArray = {"alice", "bob", "charlie"};
        String[] lastNameArray = {"delta", "eta", "feta"};

        builderUsernameAndFirstName
                .randomFrom("username", usernameArray)
                .randomFrom("firstname", firstNameArray)
                .toBeBuilt(5);

        RandomBuilder<User> builderUsernameLastName = new RandomBuilder<User>(User.class, randomizer);
        builderUsernameLastName
                .randomFrom("username", usernameArray)
                .randomFrom("lastname", lastNameArray)
                .toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(builderUsernameAndFirstName);
        runner.addBuilder(builderUsernameLastName);

        List<User> builtUsers = runner.build();

        int numberOfUsersWithUsernameAndFirstName = 0;
        int numberOfUsersWithUsernameAndLastName = 0;

        List<String> usernames = Arrays.asList(usernameArray);
        List<String> firstNames = Arrays.asList(firstNameArray);
        List<String> lastNames = Arrays.asList(lastNameArray);

        for (User u : builtUsers) {
            Assert.assertTrue(usernames.contains(u.getUsername()));
            if (u.getFirstname() == null) {
                Assert.assertTrue(lastNames.contains(u.getLastname()));
                numberOfUsersWithUsernameAndLastName++;
            } else {
                Assert.assertTrue(firstNames.contains(u.getFirstname()));
                numberOfUsersWithUsernameAndFirstName++;
            }
        }
        Assert.assertEquals(5, numberOfUsersWithUsernameAndFirstName);
        Assert.assertEquals(3, numberOfUsersWithUsernameAndLastName);
    }

    @Test
    public void should_allow_setting_completelly_different_fields_in_different_builders() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> builderUsernameBalance = new RandomBuilder<User>(User.class, randomizer);

        String[] usernameArray = {"destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero" };
        String[] firstNameArray = {"alice", "bob", "charlie"};

        builderUsernameBalance
                .randomFrom("username", usernameArray)
                .randomFromRange("accountBalance", -5.2, 3.14)
                .toBeBuilt(5);

        RandomBuilder<User> builderFirstNameNumberOfCards = new RandomBuilder<User>(User.class, randomizer);
        builderFirstNameNumberOfCards
                .randomFrom("firstname", firstNameArray)
                .randomFromRange("numberOfCards", 1L, 10L)
                .toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(builderUsernameBalance);
        runner.addBuilder(builderFirstNameNumberOfCards);

        List<User> builtUsers = runner.build();

        builtUsers.stream().forEach(System.out::println);

        int numberOfUsersWithUsernameAndAccountBalance = 0;
        int numberOfUsersWithFirstNameAndNumberOfCards = 0;

        List<String> usernames = Arrays.asList(usernameArray);
        List<String> firstNames = Arrays.asList(firstNameArray);

        for (User u : builtUsers) {
            if (u.getFirstname() != null) {
                Assert.assertTrue(firstNames.contains(u.getFirstname()));
                Assert.assertTrue(u.getNumberOfCards() >= 1L && u.getNumberOfCards() < 10L);
                numberOfUsersWithFirstNameAndNumberOfCards++;
            } else {
                Assert.assertTrue(usernames.contains(u.getUsername()));
                Assert.assertTrue(u.getAccountBalance() >= -5.2 && u.getAccountBalance() < 3.14);
                numberOfUsersWithUsernameAndAccountBalance++;
            }
        }
        Assert.assertEquals(5, numberOfUsersWithUsernameAndAccountBalance);
        Assert.assertEquals(3, numberOfUsersWithFirstNameAndNumberOfCards);
    }

}
