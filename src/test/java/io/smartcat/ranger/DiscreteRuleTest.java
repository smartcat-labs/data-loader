package io.smartcat.ranger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.model.User;

public class DiscreteRuleTest {

    @Test
    public void should_set_usernames_randomly_from_the_provided_list() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac")
                .toBeGenerated(1000).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        String[] usernames = {"destroyerOfW0rldz", "univerzalBlack", "johnycage", "subzero", "krelac"};
        List<String> allowedUsernames = new ArrayList<>(Arrays.asList(usernames));
        for (User u : aggregatedObjectGenerator) {
            String message = "username can only be from allowed set, but was: " + u.getUsername();
            Assert.assertTrue(message, allowedUsernames.contains(u.getUsername()));
        }

    }

}
