package io.smartcat.data.loader.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.RandomBuilder;
import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.util.Randomizer;
import io.smartcat.data.loader.util.RandomizerImpl;

public class DiscreteRuleBooleanTest {

    @Test
    public void should_set_boolean_property() {

        Randomizer randomizer = new RandomizerImpl();
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class, randomizer);

        List<User> builtUsers = randomUserBuilder
                .randomBoolean("maried")
                .toBeBuilt(1000).buildAll();

        boolean atLeastOneMaried = false;
        boolean atLeastOneNotMaried = false;
        for (User u : builtUsers) {
            if (u.isMaried()) {
                atLeastOneMaried = true;
            } else {
                atLeastOneNotMaried = true;
            }
        }
        Assert.assertTrue(atLeastOneMaried && atLeastOneNotMaried);
    }


}
