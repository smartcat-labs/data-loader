package io.smartcat.ranger.data.generator;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;

public class UUIDRuleTest {

    @Test
    public void should_set_uuids() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withUUID("username").toBeGenerated(10).build();
        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        aggregatedObjectGenerator.forEach(user -> Assert.assertEquals(36, user.getUsername().length()));
    }
}
