package io.smartcat.ranger.data.generator.rules;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.AggregatedObjectGenerator;
import io.smartcat.ranger.data.generator.ObjectGenerator;
import io.smartcat.ranger.data.generator.model.User;

public class RangeRuleFloatTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        Float beginingOfRange = 0.2f;
        Float endOfRange = 10.1f;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "subzero").withRanges("balanceInFloat", beginingOfRange, endOfRange)
                .toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : aggregatedObjectGenerator) {
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
        float lower1 = 0.1f;
        float upper1 = 5.0f;
        float lower2 = 10.1f;
        float upper2 = 15.2f;
        float lower3 = 20.3f;
        float upper3 = 25.4f;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withRanges("balanceInFloat", lower1, upper1, lower2, upper2, lower3, upper3).toBeGenerated(1000)
                .build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        List<User> result = aggregatedObjectGenerator.generateAll();
        Assert.assertEquals(1000, result.size());

        boolean atLeastOneInFirstRange = false;
        boolean atLeastOneInSecondRange = false;
        boolean atLeastOneInThirdRange = false;
        for (User u : result) {
            String message = "user should have balanceInFloat in range:"
                    + " [0.1, 5.0) or [10.1, 15.2) or [20.3, 25.4), but it was: " + u.getBalanceInFloat();
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
