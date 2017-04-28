package io.smartcat.ranger.data.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.util.Randomizer;
import io.smartcat.ranger.data.generator.util.RandomizerImpl;

public class DiscreteRuleTest {

    @Test
    public void should_set_usernames_randomly_from_the_provided_list() {

        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        randomUserBuilder
                .randomFrom("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac")
                .toBeBuilt(1000);

        List<User> builtUsers = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        String[] usernames = {"destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac"};
        List<String> allowedUsernames = new ArrayList<>(Arrays.asList(usernames));
        for (User u : builtUsers) {
            String message = "username can only be from allowed set, but was: " + u.getUsername();
            Assert.assertTrue(message, allowedUsernames.contains(u.getUsername()));
        }

    }

}
