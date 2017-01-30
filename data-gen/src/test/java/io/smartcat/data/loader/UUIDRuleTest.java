package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class UUIDRuleTest {

    @Test
    public void should_set_uuids() {
        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        randomUserBuilder.randomUUID("username").toBeBuilt(10);
        List<User> builtUsers = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        builtUsers.forEach(user -> Assert.assertEquals(36, user.getUsername().length()));

    }

}
