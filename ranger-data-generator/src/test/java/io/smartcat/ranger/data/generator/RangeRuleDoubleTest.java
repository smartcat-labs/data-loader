package io.smartcat.ranger.data.generator;

import org.junit.Assert;
import org.junit.Test;

import io.smartcat.ranger.data.generator.model.User;
import io.smartcat.ranger.data.generator.rules.RangeRuleDouble;

public class RangeRuleDoubleTest {

    @Test
    public void should_set_low_and_high_end_values_of_a_range() {
        Double beginingOfRange = 0.2;
        Double endOfRange = 10.1;

        ObjectGenerator<User> userGenerator = new ObjectGenerator.Builder<User>(User.class)
                .withValues("username", "subzero")
                .withRanges("accountBalance", beginingOfRange, endOfRange)
                .toBeGenerated(3).build();

        AggregatedObjectGenerator<User> aggregatedObjectGenerator = new AggregatedObjectGenerator.Builder<User>()
                .withObjectGenerator(userGenerator).build();

        boolean oneExactlyAtTheBeginingOfTheRange = false;
        boolean oneExactlyAtTheEndOfTheRange = false;

        for (User u : aggregatedObjectGenerator) {
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
