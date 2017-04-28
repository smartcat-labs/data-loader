package io.smartcat.ranger.data.generator;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.rules.RangeRuleDouble;

public class RangeRuleDoubleTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        Double beginingOfRange = 0.2;
        Double endOfRange = 10.1;

        randomUserBuilder.randomFrom("username", "subzero")
                .randomFromRange("accountBalance", beginingOfRange, endOfRange).toBeBuilt(3);

        List<User> builtUsers = new BuildRunner<User>().withBuilder(randomUserBuilder).build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : builtUsers) {
            if (u.getAccountBalance() == beginingOfRange) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }
            if (Math.abs(endOfRange - u.getAccountBalance() - RangeRuleDouble.EPSILON) <= RangeRuleDouble.EPSILON) {
                oneExactlyAtTheEndOfTheRange = true;
            }
        }

        Assert.assertTrue("One user must have accountBalance with value from the begining of the defined range.",
                oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must have accountBalance with value from the end of the defined range.",
                oneExactlyAtTheEndOfTheRange);
    }

}
