package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.ObjectGenerator;
import io.smartcat.ranger.data.generator.model.User;

public class DiscreteRuleBooleanTest {

    @Test
    public void should_set_boolean_property() {
        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withBoolean("maried").toBeGenerated(1000).build();

        List<User> result = userGenerator.generateAll();

        boolean atLeastOneMaried = false;
        boolean atLeastOneNotMaried = false;
        for (User u : result) {
            if (u.isMaried()) {
                atLeastOneMaried = true;
            } else {
                atLeastOneNotMaried = true;
            }
        }
        Assert.assertTrue(atLeastOneMaried && atLeastOneNotMaried);
    }
}
