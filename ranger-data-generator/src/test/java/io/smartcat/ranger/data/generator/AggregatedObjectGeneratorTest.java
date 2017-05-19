package io.smartcat.ranger.data.generator;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;

public class AggregatedObjectGeneratorTest {

    @Test
    public void should_allow_not_setting_a_field_in_one_of_the_object_generators() {
        List<String> usernames = Arrays.asList("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero");
        List<String> firstNames = Arrays.asList("alice", "bob", "charlie");

        ObjectGenerator<User> randomUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", usernames)
                .withValues("firstname", firstNames).toBeGenerated(100).build();

        ObjectGenerator<User> usernameOnlyUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", usernames).toBeGenerated(10).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(randomUserGenerator).withObjectGenerator(usernameOnlyUserGenerator).build();

        int numberOfUsersWithUsernameAndFirstName = 0;
        int numberOfUsersWithUsernameOnly = 0;

        for (User u : aggregatedObjectGenerator) {
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
    public void should_allow_not_setting_different_fields_in_different_object_generators() {
        List<String> usernames = Arrays.asList("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero");
        List<String> firstNames = Arrays.asList("alice", "bob", "charlie");
        List<String> lastNames = Arrays.asList("delta", "eta", "feta");

        ObjectGenerator<User> usernameAndFirstNameUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", usernames)
                .withValues("firstname", firstNames).toBeGenerated(5).build();

        ObjectGenerator<User> usernameAndLastNameUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", usernames).withValues("lastname", lastNames)
                .toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(usernameAndFirstNameUserGenerator)
                .withObjectGenerator(usernameAndLastNameUserGenerator).build();

        int numberOfUsersWithUsernameAndFirstName = 0;
        int numberOfUsersWithUsernameAndLastName = 0;

        for (User u : aggregatedObjectGenerator) {
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
    public void should_allow_setting_completelly_different_fields_in_different_object_generators() {
        List<String> usernames = Arrays.asList("destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero");
        List<String> firstNames = Arrays.asList("alice", "bob", "charlie");

        ObjectGenerator<User> usernameAndBalanceUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", usernames)
                .withRanges("accountBalance", -5.2, 3.14).toBeGenerated(5).build();

        ObjectGenerator<User> firstNameAndNumberOfCardsUserGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("firstname", firstNames)
                .withRanges("numberOfCards", 1L, 10L).toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(usernameAndBalanceUserGenerator)
                .withObjectGenerator(firstNameAndNumberOfCardsUserGenerator).build();

        int numberOfUsersWithUsernameAndAccountBalance = 0;
        int numberOfUsersWithFirstNameAndNumberOfCards = 0;

        for (User u : aggregatedObjectGenerator) {
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

    @Test
    public void should_throw_exception_for_unexisting_field() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("unexistingField", "something").toBeGenerated(100).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();
        try {
            aggregatedObjectGenerator.generateAll();
            Assert.fail("should fail silently when trying to set unexiting field.");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e != null);
        } catch (Exception e) {
            Assert.fail("Unexpected exception");
        }
    }
}
