package io.smartcat.data.loader.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.data.loader.BuildRunner;
import io.smartcat.data.loader.RandomBuilder;
import io.smartcat.data.loader.model.User;

public class RangeRuleFloatTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        Float beginingOfRange = 0.2f;
        Float endOfRange = 10.1f;

        randomUserBuilder.randomFrom("username", "subzero")
                .randomFromRange("balanceInFloat", beginingOfRange, endOfRange).toBeBuilt(3);

        BuildRunner<User> runner = new BuildRunner<>();

        runner.addBuilder(randomUserBuilder);
        List<User> builtUsers = runner.build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : builtUsers) {
            if (u.getBalanceInFloat() == beginingOfRange) {
                oneExactlyAtTheBeginingOfTheRange = true;
            }
            if (Math.abs(endOfRange - u.getBalanceInFloat() - RangeRuleFloat.EPSILON) <= RangeRuleFloat.EPSILON) {
                oneExactlyAtTheEndOfTheRange = true;
            }
        }

        Assert.assertTrue("One user must have balanceInFloat with value from the begining of the defined range.",
                oneExactlyAtTheBeginingOfTheRange);
        Assert.assertTrue("One user must have balanceInFloat with value from the end of the defined range.",
                oneExactlyAtTheEndOfTheRange);
    }

    @Test
    public void should_set_float_properties_randomly_from_multi_range() {

        RandomBuilder<User> randomUserBuilder = new RandomBuilder<User>(User.class);

        float lower1 = 0.1f;
        float upper1 = 5.0f;
        float lower2 = 10.1f;
        float upper2 = 15.2f;
        float lower3 = 20.3f;
        float upper3 = 25.4f;
        List<User> builtUsers = randomUserBuilder
                .randomFromRange("balanceInFloat", lower1, upper1, lower2, upper2, lower3, upper3).build(1000);

        Assert.assertEquals(1000, builtUsers.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        boolean atLeastOneInThirdRange = false;
        for (User u : builtUsers) {
            String message = "user should have balanceInFloat in range:"
                    + " [0.1, 5.0) or [10.1, 15.2) or [20.3, 25.4), but it was: "
                    + u.getBalanceInFloat();
            boolean inFirstRange = u.getBalanceInFloat() >= lower1 && u.getBalanceInFloat() < upper1;
            if (inFirstRange) {
                atLeastOneInFirstRange = true;
            }

            boolean inSecondRange = u.getBalanceInFloat() >= lower2 && u.getBalanceInFloat() < upper2;

            if (inSecondRange) {
                atLeastOneInSecondRange = true;
            }

            boolean inThirdRange = u.getBalanceInFloat() >= lower3 && u.getBalanceInFloat() < upper3;
            if (inSecondRange) {
                atLeastOneInThirdRange = true;
            }

            Assert.assertTrue(message, inFirstRange || inSecondRange || inThirdRange);
        }

        Assert.assertTrue(atLeastOneInFirstRange && atLeastOneInSecondRange && atLeastOneInThirdRange);

    }

}
