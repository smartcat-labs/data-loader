package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.RandomBuilder;
import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.util.Randomizer;
import io.smartcat.ranger.data.generator.util.RandomizerImpl;

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
