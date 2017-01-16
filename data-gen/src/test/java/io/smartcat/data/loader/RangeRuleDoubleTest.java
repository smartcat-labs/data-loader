package io.smartcat.data.loader;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.model.User;
import io.smartcat.data.loader.rules.RangeRuleDouble;

public class RangeRuleDoubleTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        Double beginingOfRange = 0.2;
        Double endOfRange = 10.1;

        randomUserBuilder
            .randomFrom("username", "subzero")
            .randomFromRange("accountBalance", beginingOfRange, endOfRange)
            .toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : builtUsers) {
            System.out.println("accBAl: " + u.getAccountBalance() + " , == :" + (endOfRange - u.getAccountBalance()));

            if (u.getAccountBalance() == beginingOfRange) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }
            if (endOfRange - u.getAccountBalance() < RangeRuleDouble.EPSILON) {
                oneExactlyAtTheEndOfTheRange = true;
            }
        }

        Assert.assertTrue("One user must have numberOfCards property with value 0", oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must have numberOfCards property with value 9", oneExactlyAtTheEndOfTheRange);
    }

}
