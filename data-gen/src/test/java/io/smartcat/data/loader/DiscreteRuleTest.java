package io.smartcat.data.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class DiscreteRuleTest {

    @Test
    public void should_set_usernames_randomly_from_the_provided_list() {

        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        List<User> builtUsers = randomUserBuilder
                .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac")
                .build(1000);

        String[] usernames = { "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac" };
        List<String> allowedUsernames = new ArrayList<>(Arrays.asList(usernames));
        for (User u : builtUsers) {
            String message = "username can only be from allowed set, but was: " + u.getUsername();
            Assert.assertTrue(message, allowedUsernames.contains(u.getUsername()));
        }

    }

    @Test
    public void should_correctly_calculate_precedance_in_discrete_rules() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        randomUserBuilder
                .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac")
                .randomFrom("firstname", "alice", "bob", "charlie").toBeBuilt(1000);

        RandomBuilder<User> exclusiveBuilder = new RandomBuilder<User>(User.class, randomizer);
        exclusiveBuilder.exclusiveRandomFrom("username", "krelac").randomFrom("firstname", "delta").toBeBuilt(500);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        runner.addBuilder(exclusiveBuilder);

        List<User> userList = runner.build();

        for (User u : userList) {
            // because "krelac" is exclusive
            String[] nonexclusiveUsernames = { "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero" };
            List<String> allowedUsernames = new ArrayList<>(Arrays.asList(nonexclusiveUsernames));
            String[] firstnames = { "alice", "bob", "charlie" };
            List<String> allowedFirstnames = new ArrayList<>(Arrays.asList(firstnames));

            if (u.getUsername().equals("krelac")) {
                Assert.assertEquals(u.getFirstname(), "delta");
            } else {
                u.getUsername();
                String messageUsername = "username should be from the allowed set, but was: " + u.getUsername();
                Assert.assertTrue(messageUsername, allowedUsernames.contains(u.getUsername()));
                String messageFirstName = "firstname should be from the allowed set, but was: " + u.getFirstname();
                Assert.assertTrue(messageFirstName, allowedFirstnames.contains(u.getFirstname()));
            }
        }
    }

}
